package practica5
// Ejercicio 2
/*2. Se quiere simular el comportamiento del nivel de agua en un lago. El lago recibe agua desde
dos ríos y puede bajar su nivel a través de la apertura de dos presas de acuerdo con la figura.
El ejercicio también puede implementarse con un río y dos presas. Modelar utilizando espera
activa el sistema propuesto teniendo en cuenta los siguientes aspectos:
- Modelar el sistema con una hebra para cada río, una hebra para cada presa y un objeto
  que tiene una variable de tipo entero que representa el nivel de agua en el lago.
- Se quiere tener un control exacto del nivel de agua. Por ello, las variaciones en el nivel
  (incrementos o decrementos) deben realizarse en exclusión mutua.
- Cada río incrementará el nivel de agua 1000 veces (1 unidad en cada acceso a la zona
  de exclusión mutua). En el caso de que haya un solo río, éste deberá incrementar el
  nivel del agua 2000 veces.
- Cada presa disminuirá el nivel de agua 1000 veces (1 unidad en cada acceso a la zona
  de exclusión mutua).
- Si el nivel de agua es 0 las presas no pueden disminuir el nivel de agua.
- Se debe mostrar el nivel de agua al finalizar el programa (el cual deberá ser 0 tras los
  2000 incrementos y los 2000 decrementos). */
object Ejercicio2{
  // Condición sincronización: esperan si el lago está vacío
  // Exclusión mutua sobre nivel
  /*private var nivel = 0
  @volatile private var fp0 = false // Si la presa 0 quiere tomar turno
  @volatile private var fp1 = false // Si la presa 1 quiere tomar turno
  @volatile private var turnop = 0 // Turno para cada presa

  @volatile private var fp = false // La presa quir
  @volatile private var fr = false // Si el río quiere tomar turno
  @volatile private var turnopr = 0 // turno presa = 0, turno río = 1

  def incrementar() = {
    fr = true
    turnopr = 0
    while(fp && turnopr == 0) Thread.sleep(0) //
    nivel += 1
    println(s"Nivel = $nivelAgua")
    fr = false
  }

  def decrementar0() = {
    while(nivel == 0) Thread.sleep(0)
    fp0 = true
    turnop = 1
    while(fp1 && turnopr == 1) Thread.sleep(0)
    // while(nivel == 0) Thread.sleep(0)
    fp = true
    turnopr = 1
    while(fr && turnopr == 1)Thread.sleep(0)
    nivel -= 1
    println(s"Nivel = $nivelAgua")
    fp = false
    fp0 = false
  }

  def decrementar1() = {
    fp1 = true
    turnop = 0
    while (fp0 && turnop == 0) Thread.sleep(0)

    while (nivel == 0) Thread.sleep(0)
    fp = true
    turnopr = 0
    while (fr && turnopr == 0) Thread.sleep(0)
    nivel -= 1
    log(s"Nivel = $nivel")
    fp = false
    fp1 = false
  }
  def nivelLago =  nivel */
  @volatile var nvAgua = 0
  val nvMax = 1000
  val aumRio = 1000
  val disPresa = 1000
  val rios = 2
  val presas = 2
  val numProcesses = rios + presas //Se puede borrar sustituyendo por number.length
  @volatile var number = Array.fill(numProcesses)(0) // Número de turno para cada proceso
  @volatile var entering = Array.fill(numProcesses)(false) // Indica si un proceso está intentando entrar
}
object RiosYPresas {
  def main(args: Array[String]): Unit = {
    for (i <- 0 until Ejercicio2.numProcesses) {
      if (i < Ejercicio2.rios) { // Significa que va por un proceso de los ríos
        val p = new Thread{ // Preprotocolo
          for (k <- 0 until Ejercicio2.aumRio) {
      // COMPROBACIÓN de que el nivel de agua no supera el máximo ya que los ríos aportan agua al lago por lo que solo incrementarían su nivel
            while (Ejercicio2.nvAgua >= Ejercicio2.nvMax) Thread.sleep(0) // Si lo supera, espera a que una presa pueda decrementar el nivel
            Ejercicio2.entering(i) = true // intento de entrar
            Ejercicio2.number(i) = Ejercicio2.number.max + 1 // Toma el siguiente número disponible
            Ejercicio2.entering(i) = false //ya asignado su turno, espera
            // Paso 2: Esperar el turno según el número asignado-> otro proceso esta asignando su numero/ tiene un num menor
            for (j <- 0 until Ejercicio2.numProcesses) {
              while (Ejercicio2.entering(j)) Thread.sleep(0)
              while (Ejercicio2.number(j) != 0 && (Ejercicio2.number(i) > Ejercicio2.number(j) || (Ejercicio2.number(i) == Ejercicio2.number(j) && i > j))) Thread.sleep(0)
            }
            // Paso 3: Está en la sección crítica
            Ejercicio2.nvAgua += 1
            println(s"Nivel del agua= ${Ejercicio2.nvAgua}")
            // Paso 4: Postprotocolo
            Ejercicio2.number(i) = 0
          }
        }
        p.start(); p.join()
      } else { // Significa que vamos por un proceso de presas
        val p = new Thread { // Preprotocolo
          for (k <- 0 until Ejercicio2.disPresa) {
      // COMPROBACIÓN de que el nivel de agua no es negativo ya que las presas solo quitarían agua al lago por lo que solo decrementarían su nivel
            while (Ejercicio2.nvAgua <= 0) Thread.sleep(0) // Si es así, espera a que un río lo incremente
            Ejercicio2.entering(i) = true //intento de entrar
            Ejercicio2.number(i) = Ejercicio2.number.max + 1 // Toma el siguiente número disponible
            Ejercicio2.entering(i) = false //ya asignado su turno, espera
            // Paso 2: Esperar el turno según el número asignado-> otro proceso esta asignando su numero/ tiene un num menor
            for (j <- 0 until Ejercicio2.numProcesses) {
              while (Ejercicio2.entering(j)) Thread.sleep(0)
              while (Ejercicio2.number(j) != 0 && (Ejercicio2.number(i) > Ejercicio2.number(j) || (Ejercicio2.number(i) == Ejercicio2.number(j) && i > j))) Thread.sleep(0)
            }
            // Paso 3: Está en la sección crítica
            Ejercicio2.nvAgua -= 1
            println(s"Nivel del agua= ${Ejercicio2.nvAgua}")
            // Paso 4: Postprotocolo
            Ejercicio2.number(i) = 0
          }
        }
        p.start(); p.join()
      }
    }
  }
  /*def main(args: Array[String]) = {
    val rio = new Thread(() =>{
      for(i <- 0 until 2000) // Incrementar nivel de agua 2000 veces
        Lago.incrementar()
    })
    rio.start()

    val presa0 = new Thread(() => {
      for (i <- 0 until 1000) // Decremenetar nivel presa0 1000 veces
        Lago.decrementar0()
    })

    val presa1 = new Thread(() => {
      for (i <- 0 until 1000) // Decrementar nivel presa1 1000 veces
        Lago.incrementar()
    })
    rio.start(); presa0.start(); presa1.start()
    rio.join(); presa0.join(); presa1.join()
    println(s"Nivel del lago = ${Lago.nivelLago}")
  }*/

}
