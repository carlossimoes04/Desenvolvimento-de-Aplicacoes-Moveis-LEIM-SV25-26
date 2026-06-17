package dam_A51696.pantrychef

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe base da aplicação Pantry Chef
 *
 * Esta classe herda de [Application] e é o primeiro componente a ser instanciado
 * quando a aplicação é iniciada, antes de qualquer Activity, Service ou Receiver
 *
 * A anotação [HiltAndroidApp] é obrigatória e fundamental, pois desencadeia a
 * geração de código do Dagger Hilt. Ela cria um contentor de dependências ao
 * nível da aplicação (Application Component) que serve como base para injetar
 * dependências (como Repositórios, ViewModels e Bases de Dados) no resto da aplicação
 */
@HiltAndroidApp
class PantryChefApp : Application()
