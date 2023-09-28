package com.ruoyi.project.Utils;

import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.contracts.AirdropContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class Web3jUtils {
    @Autowired
    BusConfigService config;

    public static Web3j getWeb3j() {
        Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/6c2b0b9b1b5e4b6e9b0b2a1b0b2a1b0b"));
        return web3j;
    }

    public static Web3j getWeb3j(String url) {
        Web3j web3j = Web3j.build(new HttpService(url));
        return web3j;
    }

    // 获取hash
    public String checkHash(String url, String hash, String fromAddress, String contractAddress, String methodId) {
        Web3j web3j = getWeb3j(url);
        try {
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hash).sendAsync().get();
            // 如果交易成功status = 0x0，而且交易的from地址和当前地址一致，交易的to地址和合约地址一致，交易的methodID是"0xa9059cbb"，则认为质押成功，返回true，否则返回false
            if (ethSendTransaction == null) {
                return null;
            }
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(ethSendTransaction.getTransactionHash()).sendAsync().get();
            if (ethGetTransactionReceipt == null || !ethGetTransactionReceipt.getTransactionReceipt().isPresent()) {
                return null;
            }

            String status = ethGetTransactionReceipt.getTransactionReceipt().get().getStatus();
            String to = ethGetTransactionReceipt.getTransactionReceipt().get().getTo();
//            String input = ethGetTransactionReceipt.getTransactionReceipt().get();
            String from = ethGetTransactionReceipt.getTransactionReceipt().get().getFrom();
            if (status.equals("0x0") && from.equals(fromAddress) && to.equals(contractAddress)
//                    && input.substring(0, 10).equals(methodId)
            ) {
                return "true";
            } else {
                return "false";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    AirdropContract _airdrop = null;

    private AirdropContract _loadAirdrop(Web3j web3j, int chainid, String privatekey, String contract_address) {
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
