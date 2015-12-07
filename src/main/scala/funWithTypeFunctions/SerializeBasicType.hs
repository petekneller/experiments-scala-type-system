{-# LANGUAGE TypeFamilies #-}
module SerializeBasicType where

data Numeral
data Text

type family BasicType a :: *
type instance BasicType Int = Numeral
type instance BasicType Double = Numeral
type instance BasicType Char = Text
type instance BasicType String = Text
     

serialize :: Int -> BasicType Int
serialize a = undefined

serialize :: String -> BasicType String
serialize s = undefined