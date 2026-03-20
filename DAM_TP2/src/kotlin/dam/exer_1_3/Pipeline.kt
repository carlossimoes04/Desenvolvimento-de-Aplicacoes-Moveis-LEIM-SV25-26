package dam.exer_1_3

class Pipeline (val transform: (List<String>) -> List<String>){

    private val listOfSteps = mutableListOf<Pipeline>()

}