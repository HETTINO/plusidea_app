package com.example.plusidea.data.mapper

import com.example.plusidea.data.local.entity.EmpresaEntity
import com.example.plusidea.data.local.entity.LocacionEntity
import com.example.plusidea.data.local.entity.ProyectoEntity
import com.example.plusidea.domain.model.Empresa
import com.example.plusidea.domain.model.Locacion
import com.example.plusidea.domain.model.Proyecto
import com.example.plusidea.domain.model.TipoContenido

// Conversión entre entidades de Room (capa de datos) y modelos de dominio.
// Solo las implementaciones de los repositorios usan estas funciones:
// la UI y los ViewModels nunca ven una entidad de Room.

fun ProyectoEntity.toDomain(): Proyecto = Proyecto(
    id = id,
    titulo = titulo,
    tipoContenido = TipoContenido.desdeEtiqueta(tipoContenido),
    empresaId = empresaId,
    locacionId = locacionId,
    guion = guion,
    materiales = materiales,
    tiempoEstimado = tiempoEstimado,
    outfit = outfit,
    pagoAcordado = pagoAcordado,
    enlaceInspiracion = enlaceInspiracion,
    fechaGrabacion = fechaGrabacion,
    horaInicio = horaInicio
)

fun Proyecto.toEntity(): ProyectoEntity = ProyectoEntity(
    id = id,
    titulo = titulo,
    tipoContenido = tipoContenido.etiqueta,
    empresaId = empresaId,
    locacionId = locacionId,
    guion = guion,
    materiales = materiales,
    tiempoEstimado = tiempoEstimado,
    outfit = outfit,
    pagoAcordado = pagoAcordado,
    enlaceInspiracion = enlaceInspiracion,
    fechaGrabacion = fechaGrabacion,
    horaInicio = horaInicio
)

fun EmpresaEntity.toDomain(): Empresa = Empresa(id = id, nombre = nombre)

fun Empresa.toEntity(): EmpresaEntity = EmpresaEntity(id = id, nombre = nombre)

fun LocacionEntity.toDomain(): Locacion = Locacion(id = id, nombre = nombre, direccion = direccion)

fun Locacion.toEntity(): LocacionEntity = LocacionEntity(id = id, nombre = nombre, direccion = direccion)