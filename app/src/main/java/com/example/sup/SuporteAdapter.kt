package com.example.sup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SuporteAdapter(
    context: Context,
    private val quantidadeChamados: Int
) : ArrayAdapter<String>(context, R.layout.list_item) {

    override fun getCount(): Int = quantidadeChamados

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        val numeroChamado = position + 1

        view.findViewById<TextView>(R.id.tvChamado).text = "Chamado $numeroChamado"
        view.findViewById<TextView>(R.id.tvDescricao).text =
            "Descrição do problema $numeroChamado - [Detalhes do problema aqui]"

        return view
    }
}