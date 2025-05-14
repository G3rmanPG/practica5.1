package practica5
// Ejercicio 2
class Lago{
  // Condición sincronización: esperan si el lago está vacío
  // Exclusión mutua sobre nivel
  private var nivel = 0
  @volatile private var fp0 = false
  @volatile private var fp1 = false
  @volatile private var turnop = 0 // Turno para cada presa

  @volatile private var fp = false
  @volatile private var fr = false
  @volatile private var turnopr = 0 // presa = 0, río = 1

  def inc = {

    fr = true
    turnopr = 0
    while(fp && turnopr == 0) Thread.sleep(0)

    nivel += 1

    fr = false
  }

  def dec0 = {

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

  def dec1 = {

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
      for(i <- 0 until 200)
      //Lago.inc
    })
    rio.start()

    val presa0 = new Thread(() => {
      for (i <- 0 until 100)
      //Lago.dec0
    })

    val presa1 = new Thread(() => {
      for (i <- 0 until 100)
      //Lago.inc
    })
  }

  rio.join()
}
