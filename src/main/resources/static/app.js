// Posición de cada lugar en el mapa esquemático (no son coordenadas reales).
const POSICIONES = {
    PAR: [100, 70],
    BIB: [340, 70],
    BLB: [600, 70],
    LAB: [800, 190],
    AUD: [800, 360],
    ENT: [100, 330],
    BLA: [340, 260],
    CAF: [600, 300]
};

const TIPOS = { PLANO: 'Plano', RAMPA: 'Rampa', ESCALERA: 'Escalera' };
const SVG = 'http://www.w3.org/2000/svg';

const mapa = document.getElementById('mapa');
const formulario = document.getElementById('formulario');
const resultado = document.getElementById('resultado');
const listaTramos = document.getElementById('lista-tramos');

let lugares = [];
let tramos = [];

function nombre(id) {
    const lugar = lugares.find(l => l.id === id);
    return lugar ? lugar.nombre : id;
}

function elemento(etiqueta, atributos, texto) {
    const el = document.createElementNS(SVG, etiqueta);
    for (const [k, v] of Object.entries(atributos)) el.setAttribute(k, v);
    if (texto) el.textContent = texto;
    return el;
}

function dibujar(recorrido) {
    mapa.querySelectorAll('g').forEach(g => g.remove());
    const capaRuta = elemento('g', {});
    const capaTramos = elemento('g', {});
    const capaLugares = elemento('g', {});

    const enRuta = new Set(recorrido ? recorrido.tramos.map(t => t.id) : []);
    const lugaresEnRuta = new Set(recorrido ? recorrido.lugares.map(l => l.id) : []);

    for (const t of tramos) {
        const [x1, y1] = POSICIONES[t.desde];
        const [x2, y2] = POSICIONES[t.hasta];
        if (enRuta.has(t.id)) {
            capaRuta.appendChild(elemento('line', { x1, y1, x2, y2, class: 'resaltado' }));
        }
        const linea = elemento('line', { x1, y1, x2, y2, class: `tramo ${t.tipo}${t.cerrado ? ' cerrado' : ''}` });
        linea.appendChild(elemento('title', {}, `${t.id}: ${nombre(t.desde)} – ${nombre(t.hasta)}, ${TIPOS[t.tipo].toLowerCase()}, ${t.metros} m${t.cerrado ? ', CERRADO' : ''}`));
        capaTramos.appendChild(linea);

        const mx = (x1 + x2) / 2;
        const my = (y1 + y2) / 2;
        capaTramos.appendChild(elemento('text', { x: mx + 6, y: my - 6, class: 'etiqueta-tramo' },
            `${TIPOS[t.tipo]} ${t.metros} m`));
        if (t.cerrado) {
            capaTramos.appendChild(elemento('text', { x: mx + 6, y: my + 14, class: 'etiqueta-cerrado' }, '✕ CERRADO'));
        }
    }

    for (const l of lugares) {
        const [x, y] = POSICIONES[l.id];
        const g = elemento('g', { class: `lugar${lugaresEnRuta.has(l.id) ? ' en-ruta' : ''}` });
        g.appendChild(elemento('circle', { cx: x, cy: y, r: 11 }));
        const abajo = y > 300;
        g.appendChild(elemento('text', { x, y: abajo ? y + 32 : y - 20, 'text-anchor': 'middle' }, l.nombre));
        capaLugares.appendChild(g);
    }

    mapa.append(capaRuta, capaTramos, capaLugares);
}

function llenarLista() {
    listaTramos.innerHTML = '';
    for (const t of tramos) {
        const li = document.createElement('li');
        li.textContent = `${nombre(t.desde)} – ${nombre(t.hasta)}: ${TIPOS[t.tipo].toLowerCase()}, ${t.metros} m${t.cerrado ? ' (cerrado)' : ''}`;
        listaTramos.appendChild(li);
    }
}

function llenarSelects() {
    for (const id of ['origen', 'destino']) {
        const select = document.getElementById(id);
        for (const l of lugares) {
            select.appendChild(new Option(l.nombre, l.id));
        }
    }
}

function mostrar(html) {
    resultado.innerHTML = html;
}

function escapar(texto) {
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}

async function cargarMapa() {
    mostrar('<p class="vacio">Cargando el mapa…</p>');
    try {
        const respuesta = await fetch('api/mapa');
        if (!respuesta.ok) throw new Error();
        const datos = await respuesta.json();
        lugares = datos.lugares;
        tramos = datos.tramos;
        llenarSelects();
        llenarLista();
        dibujar(null);
        mostrar('<p class="vacio">Elige un origen y un destino para ver el recorrido.</p>');
    } catch {
        mostrar('<p class="error">No se pudo cargar el mapa. Revisa que el servidor esté encendido y recarga la página.</p>');
    }
}

formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const origen = formulario.origen.value;
    const destino = formulario.destino.value;
    if (!origen || !destino) {
        mostrar('<p class="error">Elige un origen y un destino.</p>');
        (origen ? formulario.destino : formulario.origen).focus();
        return;
    }
    if (origen === destino) {
        dibujar(null);
        mostrar(`<p class="ok-titulo">Ya estás en ${escapar(nombre(origen))}.</p>`);
        return;
    }

    const boton = formulario.querySelector('button');
    boton.disabled = true;
    mostrar('<p class="vacio">Buscando recorrido…</p>');

    const parametros = new URLSearchParams({ origen, destino, sinEscaleras: formulario.sinEscaleras.checked });
    try {
        const respuesta = await fetch(`api/recorridos?${parametros}`);
        const datos = await respuesta.json();
        if (!respuesta.ok) {
            dibujar(null);
            const sugerencia = respuesta.status === 422
                ? '<p class="nota">Prueba con otro destino o pide ayuda en la entrada principal.</p>'
                : '';
            mostrar(`<p class="error">${escapar(datos.mensaje)}</p>${sugerencia}`);
            return;
        }
        dibujar(datos);
        const pasos = datos.tramos.map((t, i) => `
            <li>${escapar(datos.lugares[i].nombre)} → ${escapar(datos.lugares[i + 1].nombre)}
                <span class="tramo">${TIPOS[t.tipo]}, ${t.metros} m</span></li>`).join('');
        mostrar(`
            <p class="ok-titulo">Recorrido encontrado: ${datos.metrosTotales} m</p>
            <ol>${pasos}</ol>
            <p class="nota">${escapar(datos.aviso)}</p>`);
    } catch {
        mostrar('<p class="error">No se pudo calcular el recorrido. Revisa tu conexión e intenta de nuevo.</p>');
    } finally {
        boton.disabled = false;
    }
});

cargarMapa();
