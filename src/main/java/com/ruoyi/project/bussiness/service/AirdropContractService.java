package com.ruoyi.project.bussiness.service;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;

@Service
public class AirdropContractService {

    AirdropContract _airdrop = null;

    public AirdropContract _loadAirdrop(Web3j web3j, int chainid, String privatekey, String contract_address) {
        if (_airdrop == null) {
            Credentials credentials = Credentials.create(privatekey);
            ContractGasProvider contractGasProvider = new StaticGasProvider(BigInteger.valueOf(1000000000), BigInteger.valueOf(2100000));
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, chainid); //EIP-155
            return AirdropContract.load(contract_address, web3j, transactionManager, contractGasProvider);
        }
        return _airdrop;
    }

    public static RawTransaction newRawTx(int _nonce, String _gasPrice, int _gasLimit, String _to, String _value, String _data) {
        BigInteger nonce = new BigInteger(String.valueOf(_nonce));
        BigInteger gasLimit = new BigInteger(String.valueOf(_gasLimit));
        BigInteger gasPrice = Convert.toWei(_gasPrice, Convert.Unit.GWEI).toBigInteger();
        BigInteger value = Convert.toWei(_value, Convert.Unit.WEI).toBigInteger();

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, _to, value, _data);
        return rawTransaction;
    }

}
