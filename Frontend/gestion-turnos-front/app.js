document.addEventListener('DOMContentLoaded', () => {
    console.log("🚀 Sistema de Turnos UNICEN - Frontend Activo");

    // Elementos del DOM
    const modal = document.getElementById('modalTurno');
    const btnAbrirModal = document.getElementById('btnAbrirModal');
    const btnCerrarModal = document.getElementById('btnCerrarModal');
    const btnCancelar = document.getElementById('btnCancelar');
    const formTurno = document.getElementById('formTurno');
    const selectEspecialidad = document.getElementById('selectEspecialidad');
    const selectProfesional = document.getElementById('selectProfesional');
    
    // Elementos para Horarios Dinámicos
    const selectHora = document.getElementById('turnoHora');
    const inputFecha = document.getElementById('turnoFecha');

    // Elementos para el Buscador Predictivo de Pacientes
    const btnBuscarPaciente = document.getElementById('btnBuscarPaciente');
    const inputDni = document.getElementById('pacienteDni');
    const inputNombre = document.getElementById('pacienteNombre');
    const inputApellido = document.getElementById('pacienteApellido');
    const badgePaciente = document.getElementById('badgePaciente');
    
    // Caché local para filtrar médicos en el cliente sin repetir llamadas a la API
    let profesionalesCache = [];

    // Seteamos la fecha de hoy en el dashboard
    const fechaActualEl = document.getElementById('fechaActual');
    if (fechaActualEl) {
        fechaActualEl.innerText = new Date().toLocaleDateString('es-AR');
    }

    // Funciones para controlar el Modal (Abrir / Cerrar)
    function controlarModal(abrir) {
        if (!modal) return;
        if (abrir) {
            modal.classList.remove('hidden');
            setTimeout(() => {
                modal.classList.remove('opacity-0');
                modal.querySelector('.transform').classList.remove('scale-95');
            }, 10);
        } else {
            modal.classList.add('opacity-0');
            modal.querySelector('.transform').classList.add('scale-95');
            setTimeout(() => {
                modal.classList.add('hidden');
                formTurno.reset();
                
                // Restablecemos el formulario a su estado neutro inicial habilitado
                inputNombre.disabled = false;
                inputApellido.disabled = false;
                if (badgePaciente) badgePaciente.classList.add('hidden');
                
                resetearSelectorProfesionales();
                resetearSelectorHorarios();
            }, 300);
        }
    }

    if (btnAbrirModal) btnAbrirModal.addEventListener('click', () => controlarModal(true));
    if (btnCerrarModal) btnCerrarModal.addEventListener('click', () => controlarModal(false));
    if (btnCancelar) btnCancelar.addEventListener('click', () => controlarModal(false));

    // --- 1. BUSCADOR PREDICTIVO DE PACIENTES (Padrón Interno) ---
    function buscarPacienteEnPadron() {
        if (!inputDni || !inputNombre || !inputApellido || !badgePaciente) return;
        
        const dniVal = inputDni.value.trim();
        if (dniVal === "") return;

        fetch(`http://localhost:8080/api/turnos/pacientes/buscar?dni=${dniVal}`)
            .then(res => {
                if (res.status === 404) {
                    // Caso: Paciente Nuevo
                    badgePaciente.innerText = "Paciente Nuevo";
                    badgePaciente.className = "text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-600";
                    badgePaciente.classList.remove('hidden');
                    
                    // Campos habilitados y limpios para escritura del administrativo
                    inputNombre.value = "";
                    inputApellido.value = "";
                    inputNombre.disabled = false;
                    inputApellido.disabled = false;
                    return null;
                }
                if (!res.ok) throw new Error("Error del servidor al consultar padrón");
                return res.json();
            })
            .then(paciente => {
                if (paciente) {
                    // Caso: El paciente ya tiene historia clínica en PostgreSQL
                    badgePaciente.innerText = "Paciente Registrado • Padrón Interno";
                    badgePaciente.className = "text-[10px] font-semibold px-2 py-0.5 rounded-full bg-slate-900 text-white";
                    badgePaciente.classList.remove('hidden');

                    // Seteamos los valores directamente desde la DB
                    inputNombre.value = paciente.nombre;
                    inputApellido.value = paciente.apellido;

                    // Bloqueamos los inputs para evitar discrepancias de datos
                    inputNombre.disabled = true;
                    inputApellido.disabled = true;
                }
            })
            .catch(err => console.error("❌ Error en la consulta de padrón:", err));
    }

    // Escuchadores de eventos para la búsqueda por padrón médico
    if (btnBuscarPaciente) btnBuscarPaciente.addEventListener('click', buscarPacienteEnPadron);
    
    // Soporte de usabilidad: si presiona Enter en el DNI, realiza la búsqueda predictiva sin disparar el form
    if (inputDni) {
        inputDni.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                buscarPacienteEnPadron();
            }
        });

        // Si el administrativo limpia el campo de DNI manualmente, reiniciamos el estado
        inputDni.addEventListener('input', () => {
            if (inputDni.value.trim() === "") {
                badgePaciente.classList.add('hidden');
                inputNombre.value = "";
                inputApellido.value = "";
                inputNombre.disabled = false;
                inputApellido.disabled = false;
            }
        });
    }

    // --- 2. CARGAR ESPECIALIDADES DINÁMICAMENTE ---
    function cargarEspecialidades() {
        if (!selectEspecialidad) return;

        fetch('http://localhost:8080/api/turnos/especialidades')
            .then(res => {
                if (!res.ok) throw new Error(`Status: ${res.status}`);
                return res.json();
            })
            .then(especialidades => {
                selectEspecialidad.innerHTML = '<option value="">Seleccione especialidad...</option>';
                especialidades.forEach(esp => {
                    selectEspecialidad.innerHTML += `<option value="${esp.id}">${esp.nombreEspecialidad}</option>`;
                });
                console.log("✅ Especialidades cargadas dinámicamente desde PostgreSQL.");
            })
            .catch(err => console.error("❌ Error al pedir las especialidades a Java:", err));
    }

    // --- 3. CARGAR PROFESIONALES EN CACHÉ (PASIVO) ---
    function cargarMedicosEnCache() {
        fetch('http://localhost:8080/api/turnos/profesionales')
            .then(response => {
                if (!response.ok) throw new Error(`Status: ${response.status}`);
                return response.json();
            })
            .then(profesionales => {
                profesionalesCache = profesionales;
                console.log("✅ Caché de profesionales sincronizada con PostgreSQL.");
            })
            .catch(err => {
                console.error("❌ Error al inicializar la caché de profesionales:", err);
            });
    }

    // Funciones auxiliares de UX para resetear estados
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
        
        selectProfesional.innerHTML = '<option value="">Seleccione profesional...</option>';
        selectProfesional.disabled = false;
        
        if (lista.length === 0) {
            selectProfesional.innerHTML = '<option value="">No hay profesionales para esta especialidad</option>';
            selectProfesional.disabled = true;
            return;
        }

        lista.forEach(prof => {
            const opt = document.createElement('option');
            opt.value = prof.id;
            opt.textContent = `Dr/a. ${prof.apellido}, ${prof.nombre}`;
            
            if (prof.especialidad) {
                opt.setAttribute('data-esp', prof.especialidad.id); 
            }
            selectProfesional.appendChild(opt);
        });
    }

    // --- 4. CONSULTAR HORARIOS DISPONIBLES EN TIEMPO REAL ---
    function consultarHorariosDisponibles() {
        if (!selectHora || !selectProfesional || !inputFecha) return;

        const profId = selectProfesional.value;
        const fechaElegida = inputFecha.value;

        if (!profId || profId === "" || isNaN(parseInt(profId)) || !fechaElegida || fechaElegida === "") {
            resetearSelectorHorarios();
            return;
        }

        selectHora.disabled = false;
        selectHora.innerHTML = '<option value="">Cargando agenda...</option>';

        fetch(`http://localhost:8080/api/turnos/horarios-ocupados?id_profesional=${profId}&fecha=${fechaElegida}`)
            .then(res => res.json())
            .then(horariosLibres => {
                selectHora.innerHTML = '<option value="">Seleccione hora...</option>';

                if (horariosLibres.length === 0) {
                    selectHora.innerHTML = '<option value="">Sin turnos disponibles para este día</option>';
                    selectHora.disabled = true;
                    return;
                }

                horariosLibres.forEach(hora => {
                    selectHora.innerHTML += `<option value="${hora}:00">${hora} hs</option>`;
                });
            })
            .catch(err => {
                console.error("❌ Error en la petición de horarios dinámicos:", err);
                selectHora.innerHTML = '<option value="">Error al mapear agenda</option>';
                selectHora.disabled = true;
            });
    }

    // Carga de procesos base iniciales
    cargarEspecialidades();
    cargarMedicosEnCache();
    resetearSelectorProfesionales();
    resetearSelectorHorarios();

    // --- 5. FILTRADO DINÁMICO REACTIVO (Especialidad ──► Médico) ---
    if (selectEspecialidad && selectProfesional) {
        selectEspecialidad.addEventListener('change', (e) => {
            const espIdSeleccionada = e.target.value;
            resetearSelectorHorarios();

            if (espIdSeleccionada === "") {
                resetearSelectorProfesionales();
            } else {
                const profesionalesFiltrados = profesionalesCache.filter(prof => {
                    return prof.especialidad && String(prof.especialidad.id) === String(espIdSeleccionada);
                });
                renderizarProfesionales(profesionalesFiltrados);
            }
        });
    }

    if (selectProfesional) selectProfesional.addEventListener('change', consultarHorariosDisponibles);
    if (inputFecha) inputFecha.addEventListener('input', consultarHorariosDisponibles);


    // --- 6. ENVÍO DEL FORMULARIO AL BACKEND ---
    if (formTurno) {
        formTurno.addEventListener('submit', (e) => {
            e.preventDefault();
            
            const profesionalVal = selectProfesional.value;
            if (!profesionalVal || profesionalVal === "") {
                alert("Por favor, seleccione un profesional válido de la lista.");
                return;
            }

            const horaVal = selectHora.value;
            if (!horaVal || horaVal === "") {
                alert("Por favor, seleccione un horario disponible.");
                return;
            }

            // BLINDAJE DE SEGURIDAD: Habilitamos los campos bloqueados un instante antes de capturar el payload
            // para asegurar que el motor de JS envíe los valores correctamente al constructor del JSON
            if (inputNombre) inputNombre.disabled = false;
            if (inputApellido) inputApellido.disabled = false;

            console.log("Enviando JSON a Spring Boot...");

            const turnoDataEntity = {
                fecha: inputFecha.value,
                hora: horaVal,
                prioridad: document.getElementById('turnoPrioridad').value,
                observaciones: document.getElementById('turnoObservaciones').value,
                paciente: {
                    dni: inputDni.value,
                    nombre: inputNombre.value,
                    apellido: inputApellido.value
                },
                id_profesional: parseInt(profesionalVal)
            };

            fetch('http://localhost:8080/api/turnos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(turnoDataEntity)
            })
            .then(response => {
                if (response.ok) {
                    alert("¡Turno guardado en PostgreSQL con éxito desde la Web! 🎉");
                    controlarModal(false);
                } else {
                    alert("El servidor de Java rechazó el turno. Código: " + response.status);
                    // Si falla el servidor, devolvemos el estado visual correspondiente al padrón
                    if (inputDni.value.trim() !== "" && badgePaciente.innerText.includes("Registrado")) {
                        inputNombre.disabled = true;
                        inputApellido.disabled = true;
                    }
                }
            })
            .catch(error => {
                console.error("Error al conectar con Java:", error);
                alert("No se pudo conectar con el Backend.");
                if (inputDni.value.trim() !== "" && badgePaciente.innerText.includes("Registrado")) {
                    inputNombre.disabled = true;
                    inputApellido.disabled = true;
                }
            });
        });
    }
    // --- 7. CARGAR Y ACTUALIZAR EL DASHBOARD EN TIEMPO REAL ---
    function actualizarDashboard() {
        const tablaBody = document.getElementById('tablaTurnosBody');
        const sinTurnosRow = document.getElementById('sinTurnosRow');
        const cantTurnosEl = document.getElementById('cantTurnos');
        const cantUrgenciasEl = document.getElementById('cantUrgencias');

        if (!tablaBody) return;

        fetch('http://localhost:8080/api/turnos')
            .then(res => {
                if (!res.ok) throw new Error("Error al recuperar el historial");
                return res.json();
            })
            .then(turnos => {
                // Contadores locales para las tarjetas métricas
                let totalTurnos = turnos.length;
                let totalUrgencias = 0;

                // Limpiamos el cuerpo de la tabla para evitar duplicaciones
                tablaBody.innerHTML = "";

                if (totalTurnos === 0) {
                    // Si no hay datos, volvemos a inyectar la fila vacía elegante
                    if (sinTurnosRow) tablaBody.appendChild(sinTurnosRow);
                    if (cantTurnosEl) cantTurnosEl.innerText = "0";
                    if (cantUrgenciasEl) cantUrgenciasEl.innerText = "0";
                    return;
                }

                // Recorremos la lista que nos devolvió Java
                turnos.forEach(turno => {
                    // Incrementamos el contador si la prioridad es Alta/Urgencia (valor 2)
                    if (String(turno.prioridad) === "2") {
                        totalUrgencias++;
                    }

                    // Traducimos el Enum numérico a texto legible para el administrativo
                    let textoPrioridad = "Baja";
                    let colorPrioridad = "text-slate-600 bg-slate-100";
                    if (String(turno.prioridad) === "1") {
                        textoPrioridad = "Media";
                        colorPrioridad = "text-amber-700 bg-amber-50 border border-amber-200/60";
                    } else if (String(turno.prioridad) === "2") {
                        textoPrioridad = "Urgencia";
                        colorPrioridad = "text-red-700 bg-red-50 border border-red-200/60 font-semibold";
                    }

                    // Creamos la fila para la tabla de forma manual
                    const fila = document.createElement('tr');
                    fila.className = "hover:bg-slate-50/80 transition-colors border-b border-slate-100";
                    
                    fila.innerHTML = `
                        <td class="px-6 py-4 font-medium text-slate-900">#${turno.id || turno.id_turno}</td>
                        <td class="px-6 py-4">
                            <div class="font-medium text-slate-800">${turno.paciente ? turno.paciente.apellido + ', ' + turno.paciente.nombre : 'Sin Datos'}</div>
                            <div class="text-xs text-slate-400">DNI: ${turno.paciente ? turno.paciente.dni : '--'}</div>
                        </td>
                        <td class="px-6 py-4 text-slate-600">
                            ${turno.medico ? 'Dr/a. ' + turno.medico.apellido : 'ID Profesional: ' + (turno.id_profesional || '--')}
                        </td>
                        <td class="px-6 py-4">
                            <div class="text-slate-800 font-medium">${turno.fecha}</div>
                            <div class="text-xs text-slate-400">${turno.hora.substring(0, 5)} hs</div>
                        </td>
                        <td class="px-6 py-4">
                            <span class="text-xs px-2 py-1 rounded-md ${colorPrioridad}">
                                ${textoPrioridad}
                            </span>
                        </td>
                        <td class="px-6 py-4 text-slate-500 max-w-xs truncate" title="${turno.observaciones || ''}">
                            ${turno.observaciones || '<span class="text-slate-300 italic">Ninguna</span>'}
                        </td>
                        <td class="px-6 py-4 text-center">
                            <button class="text-slate-400 hover:text-slate-600 p-1 rounded transition" title="Archivar Turno">
                                <i data-lucide="archive" class="w-4 h-4 inline"></i>
                            </button>
                        </td>
                    `;
                    tablaBody.appendChild(fila);
                });

                // Actualizamos los números grandes de las tarjetas
                if (cantTurnosEl) cantTurnosEl.innerText = totalTurnos;
                if (cantUrgenciasEl) cantUrgenciasEl.innerText = totalUrgencias;

                // Forzamos a Lucide a renderizar los íconos de las acciones nuevas
                if (typeof lucide !== 'undefined') lucide.createIcons();
            })
            .catch(err => console.error("❌ Error al poblar el panel operativo:", err));
    }

    // --- Disparadores Automáticos ---
    // 1. Cargamos el panel ni bien se abre la página web
    actualizarDashboard();
});