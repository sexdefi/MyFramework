package com.ruoyi.project.Utils;

import com.ruoyi.common.utils.CacheUtils;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.contracts.AirdropContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class web3jUtils {
    @Autowired
    BusConfigService config;

    public String airdropGasForListNew(List<Map<String, Object>> maps, String batchId) {
        if (maps == null || maps.size() == 0) {
            return "空投失败，没有符合条件的地址";
        }

        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        String privateKey = config.getConfig("PRIVATE_KEY", "");
        int chainid = config.getConfig("CHAINID", 198);
        String contractAddress = config.getConfig("AIRDROP_ADDRESS", "0xbf1517A5C733ad7ed59AF36A281F37dB8b8210bA");
        Web3j web3j = null;
        Credentials credentials = null;
        AirdropContract airdrop = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("send:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
            airdrop = _loadAirdrop(web3j, chainid, privateKey, contractAddress);
            System.out.println("airdrop:" + airdrop.getContractAddress());
        } catch (Exception e) {
            System.out.println("web3j = Web3j.build(new HttpService(url));");
            return "Web3未空或者私钥不正确";
        }


        // 定义两个list，存放所有的from_addr和gas
        List<String> from_addr_list = new ArrayList<>();
        List<BigDecimal> gas_list = new ArrayList<>();

        // 遍历maps，取出from_addr和gas，放入list中
        maps.forEach(map -> {
            // 参数校验
            if (map.get("from_addr") != null && map.get("gas") != null) {
                from_addr_list.add(map.get("from_addr").toString());

                BigDecimal bigGas = new BigDecimal(map.get("gas").toString());
                gas_list.add(bigGas);
            }
        });

        if (from_addr_list.size() != gas_list.size()) {
            System.out.println("from_addr_list.size() != gas_list.size()");
            return "空投列表异常，数量不匹配";
        }
        if (from_addr_list.size() == 0) {
            System.out.println("from_addr_list.size() == 0");
            return "空投列表为空";
        }
        if (web3j == null || credentials == null) {
            System.out.println("web3j == null || credentials == null");
            return "Web3j或者私钥不正确";
        }
        // 将gas累加，查询余额，如果余额小于gas累加，就不空投，返回false
        BigDecimal totalGas = new BigDecimal(0);
        for (BigDecimal gas : gas_list) {
            totalGas = totalGas.add(gas);
        }
        try {
            BigInteger balance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
            if (balance.compareTo(totalGas.toBigInteger()) < 0) {
                System.out.println("账户余额不足，无法空投");
                return "账户余额不足，无法空投";
            }
        } catch (Exception e) {
            System.out.println("查询余额时候，异常！");
            return "查询余额时候，异常！";
        }

        // 定义一个计数器，记录空投成功的数量
        // 将gas_list里面的数据类型从BigDecimal转换成BigInteger
        BigInteger totalGasBig = BigInteger.ZERO;
        List<BigInteger> gas_list_big = new ArrayList<>();
        for (BigDecimal gas : gas_list) {
            gas_list_big.add(gas.toBigInteger());
            totalGasBig = totalGasBig.add(gas.toBigInteger());
        }
        // 多加一点gas防止四舍五入
        totalGasBig = totalGasBig.add(new BigInteger("100000000000"));

        String hash = "";
        try {
            int _nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount().intValue();
            String _gasPrice = "1";
            int _gasLimit = 6000000;
            String _to = contractAddress;
            String _value = totalGasBig.toString();
            String _data = airdrop.multiTransfer_OST(from_addr_list, gas_list_big).encodeFunctionCall().substring(2);
            RawTransaction rawTransaction = newRawTx(_nonce, _gasPrice, _gasLimit, _to, _value, _data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainid, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();
            if (ethSendTransaction.hasError()) {
                System.out.println("空投失败");
                return "空投失败";
            }
            hash = ethSendTransaction.getTransactionHash();
            System.out.println("hash:" + hash);

            // 保存到数据库当中
            // 将gas从BigDecimal转为String类型，不用科学记数法
            String gasStr = totalGasBig.toString();
            // adDate为当前时间用yyyy-MM-dd格式化后的字符串
            String adDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            return "success," + hash;
        } catch (Exception ex) {
            System.out.println("ex:" + ex);
            return "空投失败";
        }

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

    public void addSnapshotCache(String batchid, Object value) {
        CacheUtils.put("snapshot",batchid, value);
    }
    // 获取缓存
    public Object getSnapshotCache(String batchid) {
        return CacheUtils.get("snapshot",batchid);
    }

    public void setBatchidStatusCache(String batchid, String value) {
        CacheUtils.put("batch",batchid, value);
    }

    public String getBatchidStatusCache(String batchid) {
        return (String) CacheUtils.get("batch",batchid);
    }

    public boolean isBatchidExistCache(String batchid) {
        return CacheUtils.get("batch",batchid) != null;
    }

    public void setBatchDayCache(String batchDay, int count){
        CacheUtils.put("batchDay",batchDay, count);
    }

    public int getBatchDayCache(String batchDay){
        Object o = CacheUtils.get("batchDay",batchDay);
        if(o == null){
            return -1;
        }else{
            return (int) o;
        }
    }

}
