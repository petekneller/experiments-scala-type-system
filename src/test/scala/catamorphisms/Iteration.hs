module Iteration where

import Test.Framework

type StepAlgebra b = (b, b -> b) -- the algebras, which we encode as pairs (nil, next)

data Nat = Zero | Succ Nat -- which is the initial algebra for the functor described above

foldSteps :: StepAlgebra b -> (Nat -> b) -- the catamorphisms map from Nat to b
foldSteps (nil, next) Zero          = nil
foldSteps (nil, next) (Succ nat)    = next $ foldSteps (nil, next) nat

-- example on algebra of strings
main = print $ foldSteps ("go!", \s -> "wait..." ++ s) (Succ . Succ . Succ . Succ $ Zero)

