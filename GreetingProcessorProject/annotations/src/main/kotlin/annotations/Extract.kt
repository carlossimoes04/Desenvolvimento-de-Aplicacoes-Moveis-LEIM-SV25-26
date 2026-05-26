package annotations

/**
 * Anotação personalizada para extração de dados através de Expressões Regulares (Regex)
 *
 * Funções abstratas marcadas com esta anotação farão com que o processador crie
 * uma classe concreta onde esses métodos são implementados. O método gerado
 * aplicará o [regex] a uma String de input e devolverá o resultado capturado
 *
 * @property regex A expressão regular (Regex) utilizada para procurar padrões no texto
 */
// @Target indica que esta anotação apenas pode ser colocada por cima de funções (métodos)
@Target(AnnotationTarget.FUNCTION)
// @Retention(SOURCE) faz com que a anotação só exista na altura da compilação
@Retention(AnnotationRetention.SOURCE)
// Declara a anotação 'Extract', que exige a passagem de uma String correspondente à Expressão Regular
annotation class Extract (val regex: String) {

}