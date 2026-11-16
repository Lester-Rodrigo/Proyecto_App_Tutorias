"""Generate the final pitch PDF from real application evidence."""
from pathlib import Path
from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.colors import HexColor
from reportlab.lib.utils import ImageReader, simpleSplit

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs/entrega-final/demo'
IMAGES = ROOT / 'docs/entrega-final/evidencias/1.3/dispositivo'
OUT.mkdir(parents=True, exist_ok=True)
FONT = ROOT / 'app/src/main/res/font'
pdfmetrics.registerFont(TTFont('Inter', str(FONT / 'inter_regular.ttf')))
pdfmetrics.registerFont(TTFont('InterBold', str(FONT / 'inter_semibold.ttf')))
pdfmetrics.registerFont(TTFont('Outfit', str(FONT / 'outfit_semibold.ttf')))
W, H = 1280, 720
BG, FG, MUTED, GREEN = map(HexColor, ['#F7F9F7', '#181D16', '#3F4A3D', '#006E20'])
pdf = canvas.Canvas(str(OUT / 'diapositivas-tutorlink.pdf'), pagesize=(W, H))
pdf.setTitle('TutorLink - Demo final 1.3 (4)')
pdf.setAuthor('Equipo TutorLink')

def text(value, x, y, size=26, width=900, color=FG, font='Inter', leading=None):
    pdf.setFillColor(color)
    pdf.setFont(font, size)
    for line in simpleSplit(value, font, size, width):
        pdf.drawString(x, y, line)
        y -= leading or size * 1.4
    return y

def base(number, section, title):
    pdf.setFillColor(BG)
    pdf.rect(0, 0, W, H, stroke=0, fill=1)
    text(section.upper(), 72, 655, 15, color=GREEN, font='InterBold')
    text(title, 72, 595, 42, width=1136, font='Outfit')
    text('TutorLink  /  Entrega final  /  1.3 (4)', 72, 29, 12, color=MUTED)
    pdf.drawRightString(1208, 29, f'{number:02d} / 08')

def image(path, x, y, max_w, max_h):
    img = ImageReader(str(path))
    iw, ih = img.getSize()
    scale = min(max_w / iw, max_h / ih)
    pdf.drawImage(img, x, y, width=iw*scale, height=ih*scale, mask='auto')

base(1, 'Producto', 'TutorLink')
text('Encuentra apoyo para tu próximo curso', 72, 472, 38, width=650, font='Outfit')
text('Busca un tutor, conoce su perfil y reserva una tutoría desde tu teléfono.', 72, 350, 27, width=610)
text('Demo final  |  Android 16 o superior', 72, 173, 20, color=GREEN)
image(IMAGES/'02-inicio.png', 875, 90, 260, 450)
pdf.showPage()

base(2, 'Problema', 'Elegir un tutor requiere información clara')
text('Estudiantes universitarios que necesitan apoyo en un curso.', 72, 484, 30, width=1070, font='InterBold')
y = text('Antes de decidir, necesitan saber qué materias domina el tutor, cuánto cobra y qué disponibilidad publica.', 72, 386, 28, width=1050)
text('La validación del proyecto identificó la falta de información como una dificultad al buscar tutores.', 72, y-62, 26, width=1050, color=MUTED)
text('Fuente: README, sección 1; entrevistas y task test documentados por el equipo.', 72, 95, 14, color=MUTED)
pdf.showPage()

base(3, 'Solución', 'Buscar y conocer al tutor')
text('Búsqueda por materia', 72, 466, 30, font='InterBold')
text('Encuentra tutores relacionados con el curso que necesitas.', 72, 415, 25, width=640)
text('Perfil antes de reservar', 72, 296, 30, font='InterBold')
text('Consulta materias, tarifa, calificación y disponibilidad publicada.', 72, 245, 25, width=640)
image(IMAGES/'03-busqueda.png', 875, 83, 260, 455)
pdf.showPage()

base(4, 'Demo en vivo', 'Del perfil a una reserva confirmada')
text('1. Elegir tutor\n2. Completar tema, fecha y hora\n3. Confirmar la tutoría\n4. Verla en el calendario', 72, 468, 27, width=510)
# Line breaks are explicit paragraphs for the demonstration steps.
image(IMAGES/'04-formulario.png', 662, 88, 230, 438)
image(IMAGES/'05-confirmacion.png', 959, 88, 230, 438)
text('Formulario validado', 662, 66, 17, color=GREEN)
text('Confirmación', 959, 66, 17, color=GREEN)
pdf.showPage()

base(5, 'Continuidad', 'La tutoría queda en el calendario')
text('Consulta tus próximas sesiones', 72, 468, 30, font='InterBold')
text('La reserva se refleja en Mis tutorías después de confirmar.', 72, 409, 26, width=650)
text('Cuando falta conexión', 72, 291, 30, font='InterBold')
text('El catálogo usa caché, el historial se conserva en Room y las reservas pendientes se sincronizan al recuperar la red.', 72, 232, 25, width=650)
image(IMAGES/'06-calendario.png', 875, 85, 260, 450)
pdf.showPage()

base(6, 'Arquitectura', 'Estado de pantalla y datos separados')
text('Compose  >  ViewModel  >  Repository', 72, 476, 35, font='InterBold')
text('HomeViewModel, SearchViewModel, TutorDetailViewModel y CalendarViewModel reciben repositorios por constructor.', 72, 386, 25, width=1080)
text('Firestore: catálogo, perfiles y sesiones', 72, 275, 27, color=GREEN, font='InterBold')
text('Room: historial de búsquedas  |  DataStore: preferencia del tema', 72, 218, 25, width=1100)
text('StateFlow conserva el estado; los Composables reciben datos y callbacks.', 72, 123, 24, width=1100, color=MUTED)
pdf.showPage()

base(7, 'Calidad y distribución', 'Un build firmado y disponible para pruebas')
text('31 pruebas unitarias aprobadas', 72, 472, 31, font='InterBold')
text('Build debug y release verificados; lint sin errores.', 72, 420, 25, width=1080)
text('Firebase App Distribution: 1.3 (4)', 72, 327, 31, color=GREEN, font='InterBold')
text('Correcciones del cross-review y misma clave que 1.0.', 72, 275, 25, width=1080)
text('Crashlytics registra como no fatales los errores capturados por los repositorios.', 72, 179, 25, width=1080)
text('Fuentes: informes JUnit, lint, firma y distribución en docs/entrega-final/evidencias/.', 72, 89, 14, color=MUTED)
pdf.showPage()

base(8, 'Cierre', 'Encontrar, elegir y reservar')
text('Información del tutor antes de tomar la decisión.', 72, 462, 31, width=1090)
text('Una reserva que puedes localizar después.', 72, 367, 31, width=1090)
text('Un flujo preparado para probarse con usuarios reales.', 72, 272, 31, width=1090, color=GREEN, font='InterBold')
text('Código, evidencias y documentación', 72, 145, 21, color=MUTED)
url='https://github.com/Lester-Rodrigo/Proyecto_App_Tutorias/tree/entrega-final'
text('github.com/Lester-Rodrigo/Proyecto_App_Tutorias', 72, 106, 22, color=GREEN)
pdf.linkURL(url, (72, 95, 1070, 134), relative=0)
pdf.showPage()
pdf.save()
print(OUT/'diapositivas-tutorlink.pdf')
