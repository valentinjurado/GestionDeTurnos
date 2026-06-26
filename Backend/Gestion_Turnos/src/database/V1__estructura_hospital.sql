create table public.pacientes
(
    id_paciente      serial
        primary key,
    dni              varchar(20)  not null
        unique,
    nombre           varchar(100) not null,
    apellido         varchar(100) not null,
    telefono         varchar(20),
    email            varchar(100),
    fecha_nacimiento date
);

alter table public.pacientes
    owner to postgres;

create table public.especialidades
(
    id_especialidad     serial
        primary key,
    nombre_especialidad varchar(100) not null
        unique
);

alter table public.especialidades
    owner to postgres;

create table public.profesionales
(
    id_profesional  serial
        primary key,
    nombre          varchar(100) not null,
    apellido        varchar(100) not null,
    id_especialidad integer
        constraint fk_especialidad
            references public.especialidades
            on delete set null
);

alter table public.profesionales
    owner to postgres;

create table public.turnos
(
    id_turno       serial
        primary key,
    id_paciente    integer not null
        constraint fk_turno_paciente
            references public.pacientes
            on delete cascade,
    id_profesional integer not null
        constraint fk_turno_profesional
            references public.profesionales
            on delete cascade,
    fecha          date    not null,
    hora           time    not null,
    prioridad      integer default 0,
    observaciones  text
);

alter table public.turnos
    owner to postgres;

create table public.dias_semana
(
    id_dia     integer     not null
        primary key,
    nombre_dia varchar(15) not null
        unique
);

alter table public.dias_semana
    owner to postgres;

create table public.agendas_medicas
(
    id_agenda      serial
        primary key,
    id_profesional integer  not null
        constraint fk_agenda_profesional
            references public.profesionales
            on delete cascade,
    id_dia         integer  not null
        constraint fk_agenda_dia
            references public.dias_semana,
    hora_inicio    time     not null,
    hora_fin       time     not null,
    duracion_turno interval not null
        constraint chk_duracion_valida
            check (duracion_turno > '00:00:00'::interval),
    constraint uq_profesional_dia
        unique (id_profesional, id_dia),
    constraint chk_horario_valido
        check (hora_fin > hora_inicio)
);

alter table public.agendas_medicas
    owner to postgres;

