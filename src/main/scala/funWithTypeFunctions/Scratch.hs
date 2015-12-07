{-# LANGUAGE MultiParamTypeClasses, FlexibleInstances, TypeFamilies, ScopedTypeVariables #-}
module Scratch where


data Zero = Zero
data Succ a = Succ a

type One = Succ Zero
type Two = Succ One

class Inc a where
  type Res a :: *
  inc :: a -> Res a

instance Inc Zero where
  type Res Zero = One
  inc Zero = Succ Zero
  
instance Inc a => Inc (Succ a) where
  type Res (Succ a) = Succ (Succ a)
  inc (Succ a) = Succ (Succ a)
