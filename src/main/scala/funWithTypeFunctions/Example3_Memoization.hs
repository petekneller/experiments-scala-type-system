{-
 From the paper 'Fun with type functions'; SPJ, Shan, Kiselyov
-}

{-# LANGUAGE TypeFamilies, MultiParamTypeClasses, FlexibleInstances #-}
module Example3_Memoization where

class Memo a where
  data Table a :: * -> *
  toTable :: (a -> w) -> Table a w
  fromTable :: Table a w -> (a -> w)

instance Memo Bool where
  data Table Bool w = TBool w w
  toTable f = TBool (f True) (f False)
  fromTable (TBool x y) b = if b then x else y

f :: Bool -> Int
f True = undefined -- factorial 100
f False = undefined -- fibonacci 100

g :: Bool -> Int
g = fromTable (toTable f)

instance (Memo a, Memo b) => Memo (Either a b) where
  data Table (Either a b) w = TSum (Table a w) (Table b w)
  toTable f = TSum (toTable (f . Left)) (toTable (f . Right))
  fromTable (TSum t _) (Left v) = fromTable t v
  fromTable (TSum _ t) (Right v) = fromTable t v

instance (Memo a, Memo b) => Memo (a,b) where
  newtype Table (a,b) w = TProduct (Table a (Table b w))
  toTable f = TProduct (toTable (\x -> toTable (\y -> f (x,y))))
  fromTable (TProduct t) (x,y) = fromTable (fromTable t x) y

instance (Memo a) => Memo [a] where
  data Table [a] w = TList w (Table a (Table [a] w))
  toTable f = TList (f [])
    (toTable (\x -> toTable (\xs -> f (x:xs))))
  fromTable (TList t _) []     = t
  fromTable (TList _ t) (x:xs) = fromTable (fromTable t x) xs
