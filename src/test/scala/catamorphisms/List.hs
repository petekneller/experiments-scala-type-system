module List where

type ContainerAlgebra a b = (b, a -> b -> b) -- f-algebra, encoded as (nil, merge)

data List a = Nil | Cons a (List a) -- which turns out to be the intial algebra

foldrList :: ContainerAlgebra a b -> (List a -> b) -- catamorphisms map from (List a) to b
foldrList (nil, merge) Nil          = nil
foldrList (nil, merge) (Cons x xs)  = merge x $ foldrList (nil, merge) xs

main = print $ foldrList (3, \x -> \y -> x*y) (Cons 10 $ Cons 100 $ Cons 1000 Nil)