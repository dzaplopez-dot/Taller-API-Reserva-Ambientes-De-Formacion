# AgendaSENA - API de Reserva de Ambientes de Formación

API REST desarrollada para resolver un problema recurrente en el centro de formación: las reservas de ambientes (salas, laboratorios, auditorios) se gestionaban manualmente por WhatsApp y hojas de cálculo, generando cruces de horario y reservas con capacidad insuficiente.

**AgendaSENA** centraliza la gestión de reservas y hace cumplir las reglas de negocio directamente en el servidor, sin confiar en validaciones del lado del cliente.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA
- Spring Validation
- Lombok
- H2 Database (en memoria)
- Maven

---

## Requisitos previos

- JDK 21 instalado
- Maven (o usar el wrapper `mvnw` incluido en el proyecto)
- Un cliente HTTP para probar la API (Postman, Thunder Client, Insomnia, etc.)
- (Opcional) Un navegador web para acceder a la consola H2

---

## Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/dzaplopez-dot/Taller-API-Reserva-Ambientes-De-Formacion.git
cd Taller-API-Reserva-Ambientes-De-Formacion
2. Ejecutar el proyecto
En Linux/Mac:

bash
./mvnw spring-boot:run
En Windows:

bash
mvnw.cmd spring-boot:run
3. La aplicación quedará disponible en:
text
http://localhost:8080
Base de datos H2
El proyecto usa H2 en memoria. La consola web de H2 está habilitada y se puede acceder en:

text
http://localhost:8080/h2-console
Datos de conexión:
Campo	Valor
JDBC URL	jdbc:h2:mem:agendasena;DB_CLOSE_DELAY=-1
User Name	sa
Password	(vacío)
Nota: La base de datos está en memoria, por lo que los datos se borran al detener la aplicación.

Configuración en application.properties:
properties
spring.application.name=agendasena
spring.datasource.url=jdbc:h2:mem:agendasena;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
Endpoints disponibles
Ambientes
Método	Endpoint	Descripción
POST	/api/ambientes	Crea un nuevo ambiente
GET	/api/ambientes	Lista todos los ambientes
GET	/api/ambientes/disponibles?inicio={fecha}&fin={fecha}	Lista ambientes disponibles en un rango de tiempo
GET	/api/ambientes/{id}/reservas?fecha={fecha}	Lista las reservas activas de un ambiente en una fecha específica
Reservas
Método	Endpoint	Descripción
POST	/api/reservas	Crea una nueva reserva aplicando las 7 reglas de negocio
PATCH	/api/reservas/{id}/cancelar	Cancela una reserva activa
Ejemplos de peticiones
Crear un ambiente
http
POST /api/ambientes
Content-Type: application/json

{
    "nombre": "Sala 101",
    "tipo": "SALA",
    "capacidad": 30
}
Respuesta exitosa (201 Created):

json
{
    "id": 1,
    "nombre": "Sala 101",
    "tipo": "SALA",
    "capacidad": 30,
    "activo": true
}
Crear una reserva
http
POST /api/reservas
Content-Type: application/json

{
    "ambienteId": 1,
    "instructorNombre": "Juan Pérez",
    "fechaInicio": "2026-06-25T08:00:00",
    "fechaFin": "2026-06-25T10:00:00",
    "numeroAprendices": 20
}
Respuesta exitosa (201 Created):

json
{
    "id": 1,
    "ambienteId": 1,
    "nombreAmbiente": "Sala 101",
    "instructorNombre": "Juan Pérez",
    "fechaInicio": "2026-06-25T08:00:00",
    "fechaFin": "2026-06-25T10:00:00",
    "numeroAprendices": 20,
    "estado": "ACTIVA"
}
Cancelar una reserva
http
PATCH /api/reservas/1/cancelar
Respuesta exitosa (200 OK):

json
{
    "id": 1,
    "ambienteId": 1,
    "nombreAmbiente": "Sala 101",
    "instructorNombre": "Juan Pérez",
    "fechaInicio": "2026-06-25T08:00:00",
    "fechaFin": "2026-06-25T10:00:00",
    "numeroAprendices": 20,
    "estado": "CANCELADA"
}
Consultar ambientes disponibles
http
GET /api/ambientes/disponibles?inicio=2026-06-25T13:00:00&fin=2026-06-25T15:00:00
Respuesta exitosa (200 OK):

json
[
    {
        "id": 2,
        "nombre": "Laboratorio de Redes",
        "tipo": "LABORATORIO",
        "capacidad": 20,
        "activo": true
    }
]
Consultar reservas de un ambiente en una fecha
http
GET /api/ambientes/1/reservas?fecha=2026-06-25T00:00:00
Respuesta exitosa (200 OK):

json
[
    {
        "id": 1,
        "ambienteId": 1,
        "nombreAmbiente": "Sala 101",
        "instructorNombre": "Juan Pérez",
        "fechaInicio": "2026-06-25T08:00:00",
        "fechaFin": "2026-06-25T10:00:00",
        "numeroAprendices": 20,
        "estado": "ACTIVA"
    }
]
Reglas de negocio
Toda reserva se valida en el servidor antes de ser persistida. Las reglas aplicadas son:

#	Regla	Descripción
1	Sin cruces de horario	Un ambiente no puede tener dos reservas ACTIVAS que se solapen en el tiempo
2	Capacidad suficiente	El número de aprendices no puede superar la capacidad del ambiente
3	Horario institucional	La reserva debe estar entre las 6:00 y las 22:00, con duración entre 1 y 4 horas
4	Ambiente activo	No se puede reservar un ambiente marcado como inactivo (activo = false)
5	Límite diario por instructor	Un instructor no puede tener más de 3 reservas ACTIVAS en el mismo día
6	Cancelación con anticipación	Una reserva solo puede cancelarse con al menos 2 horas de anticipación a su inicio
7	No reservar en pasado	La fecha de inicio de la reserva debe ser posterior al momento actual
Si alguna de estas reglas se incumple, la API responde con un error describiendo la causa, sin guardar la reserva.

Manejo de errores
La API utiliza un manejo global de excepciones que garantiza respuestas consistentes ante errores.

Formato de respuesta de error:
json
{
    "status": 400,
    "message": "Descripción clara del error"
}
Códigos HTTP utilizados:
Código	Descripción
201	Recurso creado exitosamente
200	Solicitud exitosa
400	Error de validación (regla de negocio incumplida)
404	Recurso no encontrado
409	Conflicto (solapamiento, límite de instructor, cancelación inválida)
Ejemplos de errores:
Capacidad insuficiente (Regla #2):

json
{
    "status": 400,
    "message": "La capacidad del ambiente es de 30 personas. No puede reservar para 50 aprendices."
}
Solapamiento de horario (Regla #1):

json
{
    "status": 409,
    "message": "El ambiente ya está reservado en ese horario."
}
Horario fuera de rango (Regla #3):

json
{
    "status": 400,
    "message": "Las reservas solo pueden estar entre las 6:00 y las 22:00."
}
Límite de instructor excedido (Regla #5):

json
{
    "status": 409,
    "message": "El instructor ya tiene 3 reservas activas hoy."
}
Ambiente inactivo (Regla #4):

json
{
    "status": 400,
    "message": "El ambiente no está activo."
}
Estructura del proyecto
text
src/main/java/com/agendasena/agendasena/
├── controller/          # Controladores REST
│   ├── AmbienteController.java
│   └── ReservaController.java
├── dto/                 # Data Transfer Objects
│   ├── AmbienteDTO.java
│   ├── ReservaDTO.java
│   └── CrearReservaRequest.java
├── model/               # Entidades JPA
│   ├── Ambiente.java
│   ├── Reserva.java
│   ├── TipoAmbiente.java
│   └── EstadoReserva.java
├── repository/          # Repositorios Spring Data JPA
│   ├── AmbienteRepository.java
│   └── ReservaRepository.java
├── service/             # Lógica de negocio
│   ├── AmbienteService.java
│   └── ReservaService.java
├── exception/           # Manejo de excepciones
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   ├── ConflictoReservaException.java
│   ├── RecursoNoEncontradoException.java
│   └── ReglaNegocioException.java
└── AgendasenaApplication.java
Colección de pruebas (Postman)
En el repositorio se incluye el archivo AgendaSENA-Pruebas.json, una colección completa de pruebas para Postman.

Cómo importar la colección:
Abre Postman.

Haz clic en "Import" (botón en la esquina superior izquierda).

Selecciona el archivo AgendaSENA-Pruebas.json.

La colección AgendaSENA - Pruebas API aparecerá en tu lista.

Pruebas incluidas:
Carpeta	Pruebas
Ambientes	Crear ambientes (3), Listar ambientes
Regla 1	Solapamiento (debe fallar)
Regla 2	Capacidad insuficiente (debe fallar)
Regla 3	Horario 5AM (debe fallar), Duración 5h (debe fallar)
Regla 4	Ambiente inactivo (debe fallar)
Regla 5	Límite de 3 reservas por instructor
Regla 6	Cancelación (éxito y casos de error)
Regla 7	Reserva en pasado (debe fallar)
Disponibilidad	Ambientes disponibles en un rango
Todas las pruebas incluyen tests automatizados que verifican el código de estado HTTP y el mensaje de error esperado.

Autores
Proyecto desarrollado como parte del taller "API de Reserva de Ambientes de Formación" - Primer trimestre de Spring Boot.

Daniela Zapata - @dzaplopez-dot

Sebastián Ortega - @sebasortega216-create

Licencia
Este proyecto fue desarrollado con fines académicos como parte del curso de Spring Boot del SENA.

Estado del proyecto
Completado - Todas las funcionalidades y reglas de negocio implementadas y probadas.
