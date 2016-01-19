package scalazMonadStack

import org.scalatest.FunSuite

import scalaz.effect.IO
import IO._
import scalaz._
import Kleisli._

class ParameterizingTheStackTests extends FunSuite {

  // ReaderT, EitherT, IO
  type Err = String
  type Env = Map[String, String]
  type Eith[a] = EitherT[IO, String, a]
  type MyM[a] = ReaderT[Eith, Env, a]

  test("the concrete way") {

//    val reader = MonadReader[({ type L[x, y] = ReaderT[Eith, x, y] })#L, Env] // can't summon the right implicits!
    val reader = Kleisli.kleisliMonadReader[Eith, Env]
    val trans = MonadTrans[({ type L[x[_], y] = ReaderT[x, Env, y]})#L]

    def doesSomeReading(): MyM[String] = reader.asks(env => env("x").toUpperCase)

    def doesSomeIO(a: String): MyM[Int] = {
      // _ <- trans.liftM(EitherT.right[IO, Err, Unit](IO.putStrLn(a)))(Monad[IO])
      IO.putStrLn(a)
      reader.point(2)
    }

    val program: MyM[Int] = for {
      someProp <- doesSomeReading()
      i <- doesSomeIO(someProp)
    } yield i * 2

    val env = Map("x" -> "foo", "y" -> "bar")
    println(program.run(env).run.unsafePerformIO())
  }

//  trait MyReader[M[_]] extends MonadReader[({ type L[x, y] = ReaderT[Eith, x, y]})#L, Env] { // Not quite sure why this causes conflict in the API
  trait MyReader[M[_]] {
    def point[A](a: => A): M[A]
    def bind[A, B](fa: M[A])(f: A => M[B]): M[B]
    def asks[A](f: Env => A): M[A]
  }
  implicit val myReader: MyReader[MyM] = new MyReader[MyM] {
    val F = Kleisli.kleisliMonadReader[Eith, Env]
    def point[A](a: => A) = F.point(a)
    def bind[A, B](fa: MyM[A])(f: A => MyM[B]) = F.bind(fa)(f)
    def asks[A](f: Env => A): MyM[A] = F.asks(f)
  }

  test("the parameterised way") {

    def doesSomeReading[M[_]](implicit reader: MyReader[M]): M[String] = {
      val r: M[String] = reader.asks(env => env("x").toUpperCase)
      r
    }

    // Hmmm, how do I make this work
    def doesSomeReading2[M[_, _]](implicit reader: MonadReader[M, Env]): M[Env, String] = {
      val r: M[Env, String] = reader.asks(env => env("x").toUpperCase)
      r
    }

    def doesSomeIO[M[_]](a: String)(implicit applicative: Applicative[M]): M[Int] = {
      IO.putStrLn(a)
      applicative.point(2)
    }

//    implicit val F: MonadReader[({ type L[a, b] = ReaderT[Eith, a, b] })#L, Env] = Kleisli.kleisliMonadReader[Eith, Env]
//    implicit val A: Applicative[MyM] = implicitly[Applicative[MyM]]

    val program: MyM[Int] = for {
      someProp <- doesSomeReading2[({ type L[a, b] = ReaderT[Eith, a, b] })#L](Kleisli.kleisliMonadReader[Eith, Env])
//      someProp <- doesSomeReading[MyM]
      i <- doesSomeIO[MyM](someProp)
    } yield i * 2

    val env = Map("x" -> "foo", "y" -> "bar")
    println(program.run(env).run.unsafePerformIO())

  }

}
