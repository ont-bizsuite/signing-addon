OntCversion = '2.0.0'
from ontology.interop.System.Storage import GetContext, Get, Put
from ontology.interop.System.Runtime import CheckWitness, GetTime
from ontology.interop.Ontology.Runtime import Base58ToAddress
from ontology.interop.System.Action import RegisterAction

SetPayerEvent = RegisterAction("setPayer", "payer")
EmitRecordEvent = RegisterAction("emitRecord", "userAccount", "action", "now", "optional")

ZeroAddress = bytearray(b'\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00')
AddressLength = 20

Admin = Base58ToAddress('AQf4Mzu1YJrhz9f3aRkkwSm9n3qhXGSh4p')
PAYER_KEY = "payer"

LegalActionList = ["register", "login"]

def Main(operation, args):

    if operation == "setPayer":
        assert (len(args) == 1)
        payerAddress = args[0]
        return setPayer(payerAddress)

    if operation == "emitRecord":
        assert (len(args) == 3)
        userAccount = args[0]
        actionIndex = args[1]
        optional = args[2]
        return emitRecord(userAccount, actionIndex, optional)

    if operation == "getPayer":
        return getPayer()

    return False


# Admin sets the payer address who has to pay the transaction
def setPayer(payerAddress):
    assert (CheckWitness(Admin))
    assert (len(payerAddress) == AddressLength and payerAddress is not ZeroAddress)
    Put(GetContext(), PAYER_KEY, payerAddress)
    SetPayerEvent(payerAddress)
    return True


# The payer account should invoke this method to broadcast the RecordEvent to block chain
def emitRecord(userAccount, actionIndex, optional):
    assert (CheckWitness(getPayer()))
    assert (actionIndex < len(LegalActionList))
    EmitRecordEvent(userAccount, LegalActionList[actionIndex], GetTime(), optional)
    return True

# pre-invoke this method to check the payer account address
def getPayer():
    return Get(GetContext(), PAYER_KEY)

