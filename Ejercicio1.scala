package practica5

import scala.util.Random
/*1. Problema del productor-consumidor en Scala con un buffer circular de N
     posiciones. N es una constante que vale, por ejemplo: 20
        - Existe una única hebra representando al productor que irá produciendo elementos de
        tipo entero consecutivos (0, 1, 2, etc). El número Total de enteros que produce también
        es una constante, por ejemplo: 200.
        - Existe un buffer circular acotado compartido por el productor y el consumidor para
        producir y consumir los elementos donde se tendrán los siguientes atributos:
        -> elem: array con los elementos del buffer.
        -> p: indica la posición por la que se va produciendo.
        -> c: indica la posición por la que se va consumiendo.
        -> nelem: número de elementos válidos contenidos en el buffer
        - Existe un único consumidor que irá mostrando por pantalla los elementos depositados en el buffer.*/
object Buffer1{
  private val N = 20
  private val buffer = new Array[Int](N)
  private var i = 0
  private var j = 0
  private var numElems = 0
  @volatile private var fp = false // indica si el productor quiere producir
  @volatile private var fc = false //   "    "  "  consumidor   "   consumir
  @volatile private var turno = 0 // 0 = productor, 1 = consumidor

  def nuevoDato(dato: Int) = {
    // Condición sincronización productor   
    while (numElems == N) Thread.sleep(0)  // Mientras que esté el buffer lleno el productor no puede producir nuevos datos

    fp = true // el productor quiere entrar
    turno = 1 // le damos permiso al consumidor

    while (fc && turno == 1) Thread.sleep(0) // Mientras que sea el permiso del consumidor, espera
    buffer(i) == dato // Introducir dato al buffer (produce)
    i = (i + 1) % N // Envolver posición por si nos hemos pasado de la longitud del array
    numElems += 1 // Incrementar nº elementos
    fp = false // Termina el turno del productor
  }

  def extraeDato() = {
    while (numElems == N) Thread.sleep(0) // Condición sincronización consumidor

    fc = true // el consumidor quiere entrar
    turno = 0 // dar permiso al consumidor
    while (fp && turno == 0) Thread.sleep(0) // Mientras sea el turno del productor, el consumidor espera

    val dato = buffer(j) // extraer dato (consume)
    j = (j + 1) % N
    numElems -= 1
    fc = false // termina el turno del consumidor
    dato // devolver el dato (zona no crítica)
  }
}
class Ejercicio1 {
  def main(args: Array[String]) = {
    val prod = new Thread {
      for (i <- 0 until 50) {
        Thread.sleep(Random.nextInt(100))
        Buffer1.nuevoDato(i)
      }
    }

    val cons = new Thread {
      for (i <- 0 until 20) {
        val datoConsumido = Buffer1.extraeDato()
        println(s"Consumidor lee $datoConsumido")
        Thread.sleep(Random.nextInt(500))
      }
    }
    prod.start(); cons.start();
  }
}
