package app
import annotations.Extract

/**
 * Classe abstrata base para processamento de dados textuais
 *
 * Contém métodos cujas implementações práticas serão injetadas através
 * do Annotation Processor, com base no padrão Regex fornecido
 */
// define uma classe abstrata que exige desde a sua criação receber a String de 'input' a ser analisada
abstract class DataProcessor(val input: String) {

    // indica ao KAPT que neste método é feita a extração de letras ou números logo a seguir à palavra 'Name: '
    // o (\w+) é o "grupo de captura" do Regex que apanha a palavra do nome
    @Extract(regex = "Name: (\\w+)")
    // a função não tem corpo, só tem assinatura e diz que deverá devolver uma String (ou null)
    abstract fun getName(): String?

    // indica ao KAPT que neste método é feita a extração do resto da frase logo a seguir a 'Address: '
    // o (.+) é o "grupo de captura" do Regex que apanha toda a morada
    @Extract(regex = "Address: (.+)")
    abstract fun getAddress(): String?
}