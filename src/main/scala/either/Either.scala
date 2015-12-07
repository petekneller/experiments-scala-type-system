/*


How I built up Eithers for example at work

for { 
      r1 <- eithers(0) 
      r2 <- eithers(1) 
    } yield Seq(r1, r2.....) 




val s1: Either[A, Seq[B]] = for { 
      r0 <- eithers(0) 
    } yield Seq(r0) 

    val s2: Either[A, Seq[B]] = for { 
      rights <- s1 
      r1 <- eithers(1) 
    } yield rights :+ r1 

    val s3: Either[A, Seq[B]] = for { 
      rights <- s2 
      r2 <- eithers(2) 
    } yield rights :+ r2 



    
    


eithers.foldLeft(Right(Seq()): Either[A, Seq[B]]){ 
      (acc: Either[A, Seq[B]], elem: Either[A, B]) => 
        for { 
          rights <- acc 
          right <- elem 
        } yield rights :+ right 
    }

    */
