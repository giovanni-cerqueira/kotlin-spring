package br.com.alura.forum.service

import br.com.alura.forum.dto.AtualizacaoTopicoForm
import br.com.alura.forum.dto.NovoTopicoForm
import br.com.alura.forum.dto.TopicoView
import br.com.alura.forum.exception.NotFoundException
import br.com.alura.forum.mapper.TopicoFormMapper
import br.com.alura.forum.mapper.TopicoViewMapper
import br.com.alura.forum.model.Topico
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.DeleteMapping
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Service
class TopicoService(
    private var topicos: List<Topico> = ArrayList(),
    private val topicoViewMapper: TopicoViewMapper,
    private val topicoFormMapper: TopicoFormMapper,
    private val notFoundMessage: String = "Tópico Não Encontrado"
) {

    fun listar(): List<TopicoView> {
        return topicos.stream().map { topico ->
            topicoViewMapper.map(topico)
        }.collect(Collectors.toList())
    }

    fun buscarPorId(id: Long): TopicoView {
        val topico = topicos.stream().filter({ t ->
            t.id == id
        }).findFirst().orElseThrow{NotFoundException(notFoundMessage)}

        return topicoViewMapper.map(topico)
    }

    fun cadastrar(form: NovoTopicoForm): TopicoView {
        val topico = topicoFormMapper.map(form)
        topico.id = topicos.size.toLong() + 1
        topicos = topicos.plus(topico)
        return topicoViewMapper.map(topico)
    }

    fun atualizar(form: AtualizacaoTopicoForm): TopicoView {
        val topico = topicos.stream().filter({ t ->
            t.id == form.id
        }).findFirst().get()
        val topicoAtualizado = Topico(
            id = topico.id,
            titulo = form.titulo,
            mensagem = form.mensagem,
            dataCriacao = topico.dataCriacao,
            curso = topico.curso,
            autor = topico.autor,
            status = topico.status,
            respostas = topico.respostas
        )
       topicos =  topicos.minus(topico).plus(topicoAtualizado)
        return topicoViewMapper.map(topicoAtualizado)
    }

    fun deletar(id: Long){
        val topico = topicos.stream().filter({ t ->
            t.id == id
        }).findFirst().orElseThrow{NotFoundException(notFoundMessage)}
        topicos =  topicos.minus(topico)
    }
}