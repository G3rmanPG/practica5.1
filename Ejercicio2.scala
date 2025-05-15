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
object Lago{
  // Condición sincronización: esperan si el lago está vacío
  // Exclusión mutua sobre nivel
  private var nivel = 0
  @volatile private var fp0 = false // Si la presa 0 quiere tomar turno
  @volatile private var fp1 = false // Si la presa 1 quiere tomar turno
  @volatile private var turnop = 0 // Turno para cada presa

  @volatile private var fp = false
  @volatile private var fr = false // Si el río quiere tomar turno
  @volatile private var turnopr = 0 // turno presa = 0, turno río = 1

  def incrementar() = {

    fr = true //
    turnopr = 0
    while(fp && turnopr == 0) Thread.sleep(0) // Mientras que el

    nivel += 1

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

    fp = false

    fp0 = false

  }

  def decrementar1() = {

    fp1 = 1
    turnop = 0
    while(fp0 && turnop == 0) Thread.sleep(0)

    fp = true
    turnopr = 1
    while(fr && turnopr == 1) Thread.sleep(0)

    nivel -=1
    fp =false

    fp1 = false

  }

  def nivelLago =  nivel
}
object Ejercicio2 {
  def main(args: Array[String]) = {
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
    rio.join()
    presa0.join(); presa1.join()
    println(s"Nivel del lago = ${Lago.nivelLago}")
  }


}
