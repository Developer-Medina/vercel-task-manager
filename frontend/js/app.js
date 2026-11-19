const conteudo = document.getElementById('conteudo');

// Mensagens que aparecem na próxima tela (ex.: "salvo com sucesso" depois de voltar para a lista)
let proximoAviso = null;
let proximoErro = null;

/* ------------ Utilitários ------------ */

// Evita que texto digitado vire HTML
function esc(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

// Chama a API. Se der erro, lança a mensagem que o back-end mandou
async function chamar(metodo, caminho, corpo) {
    const resposta = await fetch(API + caminho, {
        method: metodo,
        headers: corpo ? { 'Content-Type': 'application/json' } : {},
        body: corpo ? JSON.stringify(corpo) : undefined
    });
    if (!resposta.ok) {
        let mensagem = 'Erro ao comunicar com o servidor';
        try {
            mensagem = (await resposta.json()).erro || mensagem;
        } catch (e) {
            // resposta sem JSON: fica a mensagem padrão
        }
        throw new Error(mensagem);
    }
    return resposta.status === 204 ? null : resposta.json();
}

function aviso(tipo, texto) {
    return texto ? `<p class="aviso ${tipo}">${esc(texto)}</p>` : '';
}

function irPara(rota, mensagem, erro) {
    proximoAviso = mensagem || null;
    proximoErro = erro || null;
    if (location.hash === '#' + rota) {
        mostrarTela();
    } else {
        location.hash = rota;
    }
}

function statusHtml(t) {
    return `<span class="status${t.concluida ? ' concluida' : ''}">${esc(t.status)}</span>`;
}

function dadosTarefa(t) {
    return `<dl>
        <dt>Matéria</dt><dd>${esc(t.materiaNome)}</dd>
        <dt>Título</dt><dd>${esc(t.titulo)}</dd>
        <dt>Descrição</dt><dd>${esc(t.descricao)}</dd>
        <dt>Data limite</dt><dd>${esc(t.dataLimite)}</dd>
        <dt>Status</dt><dd>${statusHtml(t)}</dd>
    </dl>`;
}

/* ------------ Telas ------------ */

function telaInicio() {
    conteudo.innerHTML = `
        <h2>Bem-vindo</h2>
        <p>Cadastre as matérias que você cursa e acompanhe as tarefas de estudo de cada uma.</p>
        <ul>
            <li><a href="#/materias">Matérias</a></li>
            <li><a href="#/tarefas">Tarefas</a></li>
        </ul>`;
}

async function telaMaterias(msg) {
    const materias = await chamar('GET', '/api/materias');
    const lista = materias.length === 0
        ? '<p>Nenhuma matéria encontrada</p>'
        : `<table>
            <thead><tr><th>Nome</th><th>Descrição</th></tr></thead>
            <tbody>${materias.map(m =>
                `<tr><td>${esc(m.nome)}</td><td>${esc(m.descricao)}</td></tr>`).join('')}</tbody>
        </table>`;
    conteudo.innerHTML = `
        <h2>Matérias</h2>
        ${aviso('sucesso', msg)}
        <p><a class="botao" href="#/materias/nova">Nova</a></p>
        ${lista}`;
}

function telaNovaMateria(erro, valores = {}) {
    conteudo.innerHTML = `
        <h2>Nova matéria</h2>
        ${aviso('erro', erro)}
        <form id="form">
            <label>Nome
                <input type="text" name="nome" value="${esc(valores.nome)}">
            </label>
            <label>Descrição
                <input type="text" name="descricao" value="${esc(valores.descricao)}">
            </label>
            <div class="acoes">
                <button type="submit">Salvar</button>
                <a class="botao secundario" href="#/materias">Voltar</a>
            </div>
        </form>`;
    document.getElementById('form').addEventListener('submit', async evento => {
        evento.preventDefault();
        const dados = Object.fromEntries(new FormData(evento.target));
        try {
            await chamar('POST', '/api/materias', dados);
            irPara('/materias', 'Matéria cadastrada com sucesso');
        } catch (e) {
            telaNovaMateria(e.message, dados);
        }
    });
}

async function telaTarefas(msg, erro) {
    const tarefas = await chamar('GET', '/api/tarefas');
    const lista = tarefas.length === 0
        ? '<p>Nenhuma tarefa de estudo encontrada</p>'
        : `<table>
            <thead><tr><th>Matéria</th><th>Título</th><th>Descrição</th><th>Data limite</th><th>Status</th></tr></thead>
            <tbody>${tarefas.map(t => `<tr>
                <td>${esc(t.materiaNome)}</td>
                <td><a href="#/tarefas/${t.id}">${esc(t.titulo)}</a></td>
                <td>${esc(t.descricao)}</td>
                <td>${esc(t.dataLimite)}</td>
                <td>${statusHtml(t)}</td>
            </tr>`).join('')}</tbody>
        </table>`;
    conteudo.innerHTML = `
        <h2>Tarefas de estudo</h2>
        ${aviso('sucesso', msg)}${aviso('erro', erro)}
        <p><a class="botao" href="#/tarefas/nova">Nova</a></p>
        ${lista}`;
}

async function telaNovaTarefa(erro, valores = {}) {
    const materias = await chamar('GET', '/api/materias');

    // Sem matéria não tem como criar tarefa: mostra o aviso no lugar do formulário
    if (materias.length === 0) {
        conteudo.innerHTML = `
            <h2>Nova tarefa de estudo</h2>
            ${aviso('erro', 'É preciso cadastrar uma matéria antes de criar uma tarefa de estudo')}
            <p>
                <a class="botao" href="#/materias/nova">Cadastrar matéria</a>
                <a class="botao secundario" href="#/tarefas">Voltar</a>
            </p>`;
        return;
    }

    conteudo.innerHTML = `
        <h2>Nova tarefa de estudo</h2>
        ${aviso('erro', erro)}
        <form id="form">
            <label>Matéria
                <select name="materiaId">${materias.map(m =>
                    `<option value="${m.id}"${String(m.id) === String(valores.materiaId) ? ' selected' : ''}>${esc(m.nome)}</option>`).join('')}</select>
            </label>
            <label>Título
                <input type="text" name="titulo" value="${esc(valores.titulo)}">
            </label>
            <label>Descrição
                <input type="text" name="descricao" value="${esc(valores.descricao)}">
            </label>
            <label>Data limite (dd/mm/aaaa)
                <input type="text" name="dataLimite" placeholder="dd/mm/aaaa" value="${esc(valores.dataLimite)}">
            </label>
            <div class="acoes">
                <button type="submit">Salvar</button>
                <a class="botao secundario" href="#/tarefas">Voltar</a>
            </div>
        </form>`;
    document.getElementById('form').addEventListener('submit', async evento => {
        evento.preventDefault();
        const dados = Object.fromEntries(new FormData(evento.target));
        try {
            await chamar('POST', '/api/tarefas', { ...dados, materiaId: Number(dados.materiaId) });
            irPara('/tarefas', 'Tarefa cadastrada com sucesso');
        } catch (e) {
            telaNovaTarefa(e.message, dados);
        }
    });
}

async function telaDetalheTarefa(id, erro) {
    let tarefa;
    try {
        tarefa = await chamar('GET', '/api/tarefas/' + id);
    } catch (e) {
        return irPara('/tarefas', null, e.message);
    }
    conteudo.innerHTML = `
        <h2>Tarefa de estudo</h2>
        ${aviso('erro', erro)}
        ${dadosTarefa(tarefa)}
        <div class="acoes">
            <button type="button" id="concluir">Concluir</button>
            <button type="button" id="excluir" class="perigo">Excluir</button>
            <a class="botao secundario" href="#/tarefas">Voltar</a>
        </div>`;
    document.getElementById('concluir').addEventListener('click', async () => {
        try {
            await chamar('POST', `/api/tarefas/${id}/concluir`);
            irPara('/tarefas', 'Tarefa concluída com sucesso');
        } catch (e) {
            telaDetalheTarefa(id, e.message);
        }
    });
    document.getElementById('excluir').addEventListener('click', () => irPara(`/tarefas/${id}/excluir`));
}

async function telaExcluirTarefa(id) {
    let tarefa;
    try {
        tarefa = await chamar('GET', '/api/tarefas/' + id);
    } catch (e) {
        return irPara('/tarefas', null, e.message);
    }
    conteudo.innerHTML = `
        <h2>Excluir tarefa de estudo</h2>
        <p>Deseja realmente excluir esta tarefa?</p>
        ${dadosTarefa(tarefa)}
        <div class="acoes">
            <button type="button" id="confirmar" class="perigo">Confirmar</button>
            <a class="botao secundario" href="#/tarefas/${id}">Cancelar</a>
        </div>`;
    document.getElementById('confirmar').addEventListener('click', async () => {
        try {
            await chamar('DELETE', '/api/tarefas/' + id);
            irPara('/tarefas', 'Tarefa excluída com sucesso');
        } catch (e) {
            irPara('/tarefas', null, e.message);
        }
    });
}

/* ------------ Roteador ------------ */

async function mostrarTela() {
    const msg = proximoAviso;
    const erro = proximoErro;
    proximoAviso = null;
    proximoErro = null;
    const rota = location.hash.replace(/^#/, '') || '/';
    let partes;

    try {
        if (rota === '/') return telaInicio();
        if (rota === '/materias') return await telaMaterias(msg);
        if (rota === '/materias/nova') return telaNovaMateria();
        if (rota === '/tarefas') return await telaTarefas(msg, erro);
        if (rota === '/tarefas/nova') return await telaNovaTarefa();
        if ((partes = rota.match(/^\/tarefas\/(\d+)$/))) return await telaDetalheTarefa(partes[1]);
        if ((partes = rota.match(/^\/tarefas\/(\d+)\/excluir$/))) return await telaExcluirTarefa(partes[1]);
        telaInicio();
    } catch (e) {
        conteudo.innerHTML = aviso('erro', 'Não foi possível carregar os dados. Verifique se o servidor está no ar.');
    }
}

window.addEventListener('hashchange', mostrarTela);

// Clicar no menu da tela em que já está não muda o hash, então recarrega na mão
document.querySelectorAll('header a').forEach(link => {
    link.addEventListener('click', () => {
        if (link.hash === location.hash) {
            mostrarTela();
        }
    });
});

mostrarTela();
