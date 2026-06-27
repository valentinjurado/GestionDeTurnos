document.addEventListener('DOMContentLoaded', () => {
    console.log("Sistema de Turnos UNICEN - Frontend Activo");
    const BASE_URL = "https://gestiondeturnos-dnme.onrender.com";

    // Elementos del DOM agrupados
    const modal = document.getElementById('modalTurno');
    const formTurno = document.getElementById('formTurno');
    const [btnAbrirModal, btnCerrarModal, btnCancelar] = ['btnAbrirModal', 'btnCerrarModal', 'btnCancelar'].map(id => document.getElementById(id));
    const [selectEspecialidad, selectProfesional, selectHora, inputFecha] = ['selectEspecialidad', 'selectProfesional', 'turnoHora', 'turnoFecha'].map(id => document.getElementById(id));
    const [btnBuscarPaciente, inputDni, inputNombre, inputApellido, badgePaciente, inputTelefono, inputEmail] = ['btnBuscarPaciente', 'pacienteDni', 'pacienteNombre', 'pacienteApellido', 'badgePaciente', 'pacienteTelefono', 'pacienteEmail'].map(id => document.getElementById(id));
    let profesionalesCache = [];

    // Fecha de hoy
    const fechaActualEl = document.getElementById('fechaActual');
    if (fechaActualEl) fechaActualEl.innerText = new Date().toLocaleDateString('es-AR');

    // Función auxiliar para habilitar/deshabilitar inputs rápidamente
    const toggleInputs = (disabled) => {
        if (inputNombre) inputNombre.disabled = disabled;
        if (inputApellido) inputApellido.disabled = disabled;
        if (inputTelefono) inputTelefono.disabled = disabled;
        if (inputEmail) inputEmail.disabled = disabled;
    };

    // Control del Modal con animación suave
    function controlarModal(abrir) {
        if (!modal) return;
        if (abrir) {
            modal.classList.remove('hidden');
            setTimeout(() => {
                modal.classList.remove('opacity-0');
                modal.querySelector('.transform').classList.remove('scale-95');
                modal.querySelector('.transform').classList.add('scale-100');
            }, 10);
        } else {
            modal.classList.add('opacity-0');
            modal.querySelector('.transform').classList.remove('scale-100');
            modal.querySelector('.transform').classList.add('scale-95');
            setTimeout(() => {
                modal.classList.add('hidden');
                formTurno.reset();
                toggleInputs(false);
                if (badgePaciente) badgePaciente.classList.add('hidden');
                resetearSelectorProfesionales();
                resetearSelectorHorarios();
            }, 300);
        }
    }

    [btnAbrirModal, btnCerrarModal, btnCancelar].forEach(btn => btn?.addEventListener('click', () => controlarModal(btn === btnAbrirModal)));

    // --- BUSCADOR PREDICTIVO DE PACIENTES ---
    function buscarPacienteEnPadron() {
        if (!inputDni || !inputNombre || !inputApellido || !badgePaciente) return;
        const dniVal = inputDni.value.trim();
        if (!dniVal) return;

        fetch(`${BASE_URL}/api/turnos/pacientes/buscar?dni=${dniVal}`)
            .then(res => res.status === 404 ? null : (res.ok ? res.json() : Promise.reject("Error de servidor")))
            .then(paciente => {
                badgePaciente.classList.remove('hidden');
                if (paciente) {
                    badgePaciente.innerText = "Paciente Registrado • Padrón Interno";
                    badgePaciente.className = "text-[10px] font-semibold px-2 py-0.5 rounded-full bg-indigo-600 text-white shadow-sm";
                    inputNombre.value = paciente.nombre;
                    inputApellido.value = paciente.apellido;
                    if (inputTelefono) inputTelefono.value = paciente.telefono || "";
                    if (inputEmail) inputEmail.value = paciente.email || "";
                    toggleInputs(true);
                } else {
                    badgePaciente.innerText = "Paciente Nuevo";
                    badgePaciente.className = "text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-600 border border-slate-200";
                    inputNombre.value = "";
                    inputApellido.value = "";
                    if (inputTelefono) inputTelefono.value = "";
                    if (inputEmail) inputEmail.value = "";
                    toggleInputs(false);
                }
            })
            .catch(err => console.error("Error en la consulta de padrón:", err));
    }

    btnBuscarPaciente?.addEventListener('click', buscarPacienteEnPadron);
    
    inputDni?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') { e.preventDefault(); buscarPacienteEnPadron(); }
    });

    inputDni?.addEventListener('input', () => {
        if (!inputDni.value.trim()) {
            badgePaciente.classList.add('hidden');
            inputNombre.value = "";
            inputApellido.value = "";
            if (inputTelefono) inputTelefono.value = "";
            if (inputEmail) inputEmail.value = "";
            toggleInputs(false);
        }
    });

    // --- CARGA DE CATÁLOGOS BASE ---
    function cargarEspecialidades() {
        if (!selectEspecialidad) return;
        fetch(`${BASE_URL}/api/turnos/especialidades`)
            .then(res => res.ok ? res.json() : Promise.reject(res.status))
            .then(especialidades => {
                selectEspecialidad.innerHTML = '<option value="">Seleccione especialidad...</option>' + 
                    especialidades.map(esp => `<option value="${esp.id}">${esp.nombreEspecialidad}</option>`).join('');
            })
            .catch(err => console.error("Error al pedir especialidades:", err));
    }

    function cargarMedicosEnCache() {
        fetch(`${BASE_URL}/api/turnos/profesionales`)
            .then(res => res.ok ? res.json() : Promise.reject(res.status))
            .then(profesionales => {
                profesionalesCache = profesionales;
            })
            .catch(err => console.error("Error al inicializar caché de profesionales:", err));
    }

    function resetearSelectorProfesionales() {
        if (!selectProfesional) return;
        selectProfesional.innerHTML = '<option value="">Primero seleccione una especialidad...</option>';
        selectProfesional.disabled = true;
    }

    function resetearSelectorHorarios() {
        if (!selectHora) return;
        selectHora.innerHTML = '<option value="">Defina profesional y fecha...</option>';
        selectHora.disabled = true;
    }

    function renderizarProfesionales(lista) {
        if (!selectProfesional) return;
        selectProfesional.disabled = lista.length === 0;
        selectProfesional.innerHTML = lista.length === 0 
            ? '<option value="">No hay profesionales</option>' 
            : '<option value="">Seleccione profesional...</option>' + 
              lista.map(prof => `<option value="${prof.id}" data-esp="${prof.especialidad?.id || ''}">Dr/a. ${prof.apellido}, ${prof.nombre}</option>`).join('');
    }

    // --- CONSULTAR HORARIOS ---
    function consultarHorariosDisponibles() {
        if (!selectHora || !selectProfesional || !inputFecha) return;
        const profId = selectProfesional.value;
        const fechaElegida = inputFecha.value;

        if (!profId || !fechaElegida) return resetearSelectorHorarios();

        selectHora.disabled = false;
        selectHora.innerHTML = '<option value="">Cargando agenda...</option>';

        fetch(`${BASE_URL}/api/turnos/horarios-ocupados?id_profesional=${profId}&fecha=${fechaElegida}`)
            .then(res => res.json())
            .then(horarios => {
                selectHora.disabled = horarios.length === 0;
                selectHora.innerHTML = horarios.length === 0 
                    ? '<option value="">Sin turnos disponibles</option>' 
                    : '<option value="">Seleccione hora...</option>' + 
                      horarios.map(hora => `<option value="${hora}:00">${hora} hs</option>`).join('');
            })
            .catch(err => {
                console.error("Error en horarios dinámicos:", err);
                selectHora.innerHTML = '<option value="">Error al mapear agenda</option>';
                selectHora.disabled = true;
            });
    }

    // Inicialización
    cargarEspecialidades();
    cargarMedicosEnCache();
    resetearSelectorProfesionales();
    resetearSelectorHorarios();

    // --- EVENTOS REACTIVOS ---
    selectEspecialidad?.addEventListener('change', (e) => {
        resetearSelectorHorarios();
        const espId = e.target.value;
        espId ? renderizarProfesionales(profesionalesCache.filter(p => p.especialidad && String(p.especialidad.id) === espId)) 
              : resetearSelectorProfesionales();
    });

    selectProfesional?.addEventListener('change', consultarHorariosDisponibles);
    inputFecha?.addEventListener('input', consultarHorariosDisponibles);

    // --- ENVÍO DEL FORMULARIO ---
    formTurno?.addEventListener('submit', (e) => {
        e.preventDefault();
        if (!selectProfesional.value) return alert("Seleccione un profesional válido.");
        if (!selectHora.value) return alert("Seleccione un horario disponible.");

        toggleInputs(false);

        fetch(`${BASE_URL}/api/turnos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                fecha: inputFecha.value,
                hora: selectHora.value,
                prioridad: document.getElementById('turnoPrioridad').value,
                observaciones: document.getElementById('turnoObservaciones').value,
                paciente: { 
                    dni: inputDni.value, 
                    nombre: inputNombre.value, 
                    apellido: inputApellido.value,
                    telefono: inputTelefono?.value || null,
                    email: inputEmail?.value || null
                },
                id_profesional: parseInt(selectProfesional.value)
            })
        })
        .then(res => {
            if (res.ok) {
                alert("Turno guardado con éxito.");
                controlarModal(false);
                actualizarDashboard();
            } else {
                alert("El servidor rechazó el turno. Código: " + res.status);
                if (badgePaciente.innerText.includes("Registrado")) toggleInputs(true);
            }
        })
        .catch(err => {
            console.error("Error al conectar con Java:", err);
            alert("No se pudo conectar con el Backend.");
            if (badgePaciente.innerText.includes("Registrado")) toggleInputs(true);
        });
    });

    // --- CARGAR DASHBOARD (DISEÑO RENOVADO) ---
    function actualizarDashboard() {
        const tablaBody = document.getElementById('tablaTurnosBody');
        const [cantTurnosEl, cantUrgenciasEl] = ['cantTurnos', 'cantUrgencias'].map(id => document.getElementById(id));
        if (!tablaBody) return;

        fetch(`${BASE_URL}/api/turnos`)
            .then(res => res.ok ? res.json() : Promise.reject("Error de historial"))
            .then(turnos => {
                const urgencias = turnos.filter(t => String(t.prioridad) === "2").length;
                tablaBody.innerHTML = "";

                if (turnos.length === 0) {
                    const sinTurnosRow = document.getElementById('sinTurnosRow');
                    if (sinTurnosRow) tablaBody.appendChild(sinTurnosRow);
                    if (cantTurnosEl) cantTurnosEl.innerText = "0";
                    if (cantUrgenciasEl) cantUrgenciasEl.innerText = "0";
                    return;
                }

                // Paleta de colores para los badges de prioridad
                const configPrio = {
                    "1": { texto: "Media", css: "text-amber-700 bg-amber-100 border-amber-300 shadow-sm" },
                    "2": { texto: "Urgencia", css: "text-red-700 bg-red-100 border-red-300 shadow-sm font-bold animate-pulse" }
                };
                
                tablaBody.innerHTML = turnos.map(turno => {
                    const prio = configPrio[String(turno.prioridad)] || { texto: "Baja", css: "text-emerald-700 bg-emerald-100 border-emerald-300 shadow-sm" };
                    
                    // Manejo seguro de variables de paciente
                    const pacienteNombre = turno.paciente ? `${turno.paciente.apellido}, ${turno.paciente.nombre}` : 'Sin Datos';
                    const pacienteDni = turno.paciente && turno.paciente.dni ? turno.paciente.dni : 'DNI No registrado';
                    const pacienteTel = turno.paciente && turno.paciente.telefono ? turno.paciente.telefono : 'Sin teléfono';
                    const pacienteEmail = turno.paciente && turno.paciente.email ? turno.paciente.email : 'Sin email';
                    
                    const profesionalNombre = turno.profesional ? `Dr/a. ${turno.profesional.apellido}` : `ID Profesional: ${turno.id_profesional || '--'}`;

                    return `
                        <tr class="group hover:bg-indigo-50/40 hover:shadow-md transition-all duration-300 ease-in-out border-b border-slate-100 transform hover:-translate-y-0.5 cursor-pointer">
                            <td class="px-6 py-5 font-bold text-slate-400 group-hover:text-indigo-400 transition-colors">
                                #${turno.idTurno || '--'}
                            </td>
                            <td class="px-6 py-5">
                                <div class="font-bold text-slate-800 text-sm mb-1 group-hover:text-indigo-700 transition-colors">${pacienteNombre}</div>
                                <div class="flex flex-col gap-1 text-[11px] text-slate-500">
                                    <span class="flex items-center gap-1.5"><i data-lucide="id-card" class="w-3 h-3 text-slate-400"></i> ${pacienteDni}</span>
                                    <span class="flex items-center gap-1.5"><i data-lucide="phone" class="w-3 h-3 text-slate-400"></i> ${pacienteTel}</span>
                                    <span class="flex items-center gap-1.5"><i data-lucide="mail" class="w-3 h-3 text-slate-400"></i> ${pacienteEmail}</span>
                                </div>
                            </td>
                            <td class="px-6 py-5">
                                <div class="flex items-center gap-2">
                                    <div class="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-slate-500 font-bold text-xs">
                                        ${turno.profesional ? turno.profesional.apellido.charAt(0) : '?'}
                                    </div>
                                    <span class="font-medium text-slate-700">${profesionalNombre}</span>
                                </div>
                            </td>
                            <td class="px-6 py-5">
                                <div class="text-slate-800 font-semibold flex items-center gap-2">
                                    <i data-lucide="calendar" class="w-4 h-4 text-indigo-500"></i>
                                    ${turno.fecha || '--'}
                                </div>
                                <div class="text-xs text-slate-500 mt-1 ml-6 font-medium">
                                    ${turno.hora ? turno.hora.substring(0, 5) : '--'} hs
                                </div>
                            </td>
                            <td class="px-6 py-5">
                                <span class="text-[11px] px-2.5 py-1 rounded-full border ${prio.css}">
                                    ${prio.texto}
                                </span>
                            </td>
                            <td class="px-6 py-5">
                                <div class="text-sm text-slate-600 max-w-xs truncate bg-slate-50 p-2 rounded border border-slate-100 group-hover:bg-white transition-colors" title="${turno.observaciones || ''}">
                                    ${turno.observaciones || '<span class="text-slate-400 italic">Sin observaciones</span>'}
                                </div>
                            </td>
                            <td class="px-6 py-5 text-center">
                                <button class="text-slate-300 hover:text-red-500 hover:bg-red-50 p-2 rounded-lg transition-all duration-200" title="Archivar/Eliminar Turno">
                                    <i data-lucide="trash-2" class="w-4 h-4 inline"></i>
                                </button>
                            </td>
                        </tr>
                    `;
                }).join('');

                if (cantTurnosEl) cantTurnosEl.innerText = turnos.length;
                if (cantUrgenciasEl) cantUrgenciasEl.innerText = urgencias;
                if (typeof lucide !== 'undefined') lucide.createIcons();
            })
            .catch(err => console.error("Error al poblar el panel operativo:", err));
    }

    actualizarDashboard();
});