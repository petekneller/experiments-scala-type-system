package free

import scalaz.{ Free, Functor, Coproduct}
import scalaz.effect.IO

// Scala port of the example in http://degoes.net/articles/modern-fp

object JohnDeGoes {
  // to allow me to use the same types as his haskell example
  type Path = String
  type Bytes = Seq[Byte]
  sealed trait Level
  case object Debug extends Level


  // an algebra for interacting with files in the cloud
  sealed trait CloudFilesF[A]
  case class SaveFile[A](path: Path, data: Bytes, a: A) extends CloudFilesF[A]
  case class ListFiles[A](path: Path, f: List[Path] => A) extends CloudFilesF[A]


  // the API for the cloud files
  type CloudFilesAPI[A] = Free[CloudFilesF, A]

  def saveFile(path: Path, bytes: Bytes): CloudFilesAPI[Unit] = Free.liftF[CloudFilesF, Unit](SaveFile(path, bytes, ()))
  def listFiles(path: Path): CloudFilesAPI[List[Path]] = Free.liftF[CloudFilesF, List[Path]](ListFiles(path, identity))



  // a lower level algebra for accessing REST APIs
  sealed trait HttpF[A]
  case class GET[A](path: Path, f: Bytes => A) extends HttpF[A]
  case class PUT[A](path: Path, bytes: Bytes, f: Bytes => A) extends HttpF[A]
  case class POST[A](path: Path, bytes: Bytes, f: Bytes => A) extends HttpF[A]
  case class DELETE[A](path: Path, f: Bytes => A) extends HttpF[A]


  // we can phrase the high level API in terms of the low
  def cloudFilesI[A](cf: CloudFilesF[A]): Free[HttpF, A] = ???



  // a low level API for logging
  sealed trait LogF[A]
  case class Log[A](level: Level, msg: String, a: A) extends LogF[A]


  // and a mapping from cloud API to logging
  def logCloudFilesI[A](cf: CloudFilesF[A]): Free[LogF, Unit] = cf match {
    case SaveFile(p, _, _) => Free.liftF[LogF, Unit](Log(Debug, s"Saving file to $p", ()))
    case ListFiles(p, _) => Free.liftF[LogF, Unit](Log(Debug, s"Listing files at $p", ()))
  }


  // some helpers I need before JdGs definition of loggingCloudfilesI
  def toLeft[F[_], G[_], A](l: Free[F, A]): Free[({ type l[x] = Coproduct[F, G, x] })#l, A] = ??? // how do I do this?
  def toRight[F[_], G[_], A](r: Free[G, A]): Free[({ type l[x] = Coproduct[F, G, x] })#l, A] = ??? // how do I do this?
  def *>[F[_], A, B](f1: Free[F, A], f2: Free[F, B]): Free[F, B] = f1 flatMap (_ => f2)  // I _think_ this is what this is supposed to be

  // composition of the two interpreters
  def loggingCloudFilesI[A](op: CloudFilesF[A]): Free[({ type l[x] = Coproduct[LogF, HttpF, x] })#l, A] = *>[({ type l[x] = Coproduct[LogF, HttpF, x] })#l, Unit, A](toLeft[LogF, HttpF, Unit](logCloudFilesI(op)), toRight[LogF, HttpF, A](cloudFilesI(op)))

  // and the final interpreter (which needs to be lifted into Free)
  def executor[A](cp: Coproduct[LogF, HttpF, A]): IO[A] = ???


  // just some implicits I need to provide in order to use scalaz's Free
  implicit val fcf = new Functor[CloudFilesF] {
    def map[A, B](fa: CloudFilesF[A])(f: A => B): CloudFilesF[B] = fa match {
      case SaveFile(p, d, a) => SaveFile(p, d, f(a))
      case ListFiles(p, f2) => ListFiles(p, f2 andThen f)
    }
  }

  implicit val flog = new Functor[LogF] {
    def map[A, B](fa: LogF[A])(f: A => B): LogF[B] = fa match {
      case Log(l, m, a) => Log(l, m, f(a))
    }
  }

}
