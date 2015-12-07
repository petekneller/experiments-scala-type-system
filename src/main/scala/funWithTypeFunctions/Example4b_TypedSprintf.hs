{-
 A twist on example 4 - I've tried to convert the standalone type families into more 'traditional' type classes
-}

{-# LANGUAGE TypeFamilies, MultiParamTypeClasses, FlexibleInstances, GADTs, UndecidableInstances, InstanceSigs #-}
module Example4b_TypedSprintf where

import Data.List

data F f where
  Lit :: String -> F L
  Val :: Parser val -> Printer val -> F (V val)
  Cmp :: F f1 -> F f2 -> F (C f1 f2)

data L
data V val
data C f1 f2

type Parser a = String -> [(a,String)]
type Printer a = a -> String

-- a couple of examples of the above
-- how using a GADT and empty data decls can
-- provide an api that mimics at the type-level whats happening at the value-level
int :: F (V Int)
int = Val undefined show

f_ld :: F L
f_ld = Lit "day"

f_lds :: F (C L L)
f_lds = Cmp (Lit "day") (Lit "s")

f_dn :: F (C L (V Int))
f_dn = Cmp (Lit "day") int

f_nds :: F (C (V Int) (C L L))
f_nds = Cmp int (Cmp (Lit "day") (Lit "s"))

-- continuing sprintf
type SPrintf f = TPrinter f String

{-- this is my attempt to see how this would look with type classes, 
  instead of the original example with a standalone type family
--}
class TP tp where
  type TPrinter tp x
  printer :: F tp -> (String -> a) -> TPrinter tp a

instance TP L where
  type TPrinter L x = x
  printer :: F L -> (String -> a) -> TPrinter L a
  printer (Lit str) k = k str

instance TP (V val) where
  type TPrinter (V val) x = val -> x
  printer :: F (V val) -> (String -> a) -> TPrinter (V val) a
  printer (Val _ show) k = \x -> k (show x)
  
instance (TP f1, TP f2) => TP (C f1 f2) where
  type TPrinter (C f1 f2) x = TPrinter f1 (TPrinter f2 x)
  printer :: F (C f1 f2) -> (String -> a) -> TPrinter (C f1 f2) a
  printer (Cmp f1 f2) k = printer f1 (\s1 ->
                          printer f2 (\s2 ->
                          k (s1 ++ s2)))


sprintf :: TP f => F f -> SPrintf f
sprintf p = printer p id
{-- end my experiment --}

-- some working examples
--sprintf f_ld    -- Result: "day"
--sprintf f_lds   -- Result: "days"
--sprintf f_dn 3  -- Result: "day 3"
--sprintf f_nds 3 -- Result: "3 days"

-- scanf
type SScanf f = String -> Maybe (TParser f (), String)

type family TParser f x
type instance TParser L x = x
type instance TParser (V val) x = (x, val)
type instance TParser (C f1 f2) x = TParser f2 (TParser f1 x)

sscanf :: F f -> SScanf f
sscanf fmt inp = parser fmt () inp

parser :: F f -> a -> String -> Maybe (TParser f a, String)
parser (Lit str) v s = parseLit str v s
parser (Val reads _) v s = parseVal reads v s
parser (Cmp f1 f2) v s = case parser f1 v s of
                              Nothing -> Nothing
                              Just(v1, s1) -> parser f2 v1 s1

parseLit :: String -> a -> String -> Maybe (a, String)
parseLit str v s = case stripPrefix str s of
                        Nothing -> Nothing
                        Just s' -> Just (v, s')

parseVal :: Parser b -> a -> String -> Maybe ((a, b), String)
parseVal reads v s = case reads s of
                          [(v', s')] -> Just ((v, v'), s')
                          _ -> Nothing

-- some working examples
--sscanf f_ld "days long"  -- Result: Just((), "s long")
--sscanf f_ld "das long"   -- Result: Nothing
--sscanf f_lds "days long" -- Result: Just((), " long")
--sscanf f_dn "day 4."     -- Result: Just((),4), ".")
