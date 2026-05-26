package annotations

/**
 * Anotação personalizada para gerar um wrapper de saudação (greeting)
 *
 * Funções marcadas com esta anotação farão com que o processador crie
 * um método auxiliar que imprime a [message] antes de invocar a função original
 *
 * @property message A mensagem de saudação a ser impressa no terminal
 */
// @Target indica onde esta anotação pode ser usada. Neste caso, apenas em funções (métodos)
@Target(AnnotationTarget.FUNCTION)
// @Retention(SOURCE) significa que a anotação será descartada depois do código ser compilado,
// existindo apenas durante a escrita e processamento, não sobrecarregando a aplicação a correr
@Retention(AnnotationRetention.SOURCE)
// Declara a anotação, exigindo a um argumento 'message' do tipo String
annotation class Greeting(val message: String) {

}