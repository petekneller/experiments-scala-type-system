{-
 From the paper 'Fun with type functions'; SPJ, Shan, Kiselyov
-}

{-# LANGUAGE TypeFamilies, MultiParamTypeClasses, FlexibleInstances #-}

module Example1_Arithmetic where

class Add a b where
  type ResType a b :: *
  add :: a -> b -> ResType a b

{- My notes:
  its a pity that the ResType alias needs to repeat the type params from the class/instance def;
this isnt necessary in scala since you always refer to the alias via the enclosing typeclass trait
ie. the typeclass trait 'captures' the params so are not required when using, hence when declaring the alias;
but of course in haskell you refer to the type alias 'raw' so you need to type params to identify the class instance
-}

instance (Num a) => Add a a where
  type ResType a a = a
  add = (+)

instance Add Integer Double where
  type ResType Integer Double = Double
  add a b = fromInteger a + b

instance Add Double Integer where
  type ResType Double Integer = Double
  add a b = a + fromInteger b


-- add (3 :: Integer) (4 :: Double)