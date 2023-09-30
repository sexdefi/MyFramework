package com.ruoyi.project.bussiness.service;

import com.ruoyi.framework.aspectj.lang.annotation.DataSource;
import com.ruoyi.framework.aspectj.lang.enums.DataSourceType;
import com.ruoyi.project.bussiness.common.BusConfigService;
import com.ruoyi.project.bussiness.contracts.StakeBrc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
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
import java.util.List;
import java.util.Map;

@Service
@DataSource(value = DataSourceType.SLAVE)
public class GasGiftService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BusConfigService config;

    public String sumGasSQL =
            "SELECT from_addr,sum(gas_used * gas_price) AS gas FROM transaction_info WHERE  from_addr = '%s' AND `TIMESTAMP` >= %d AND txstatus = '0x1' GROUP BY from_addr;";

    @DataSource(value = DataSourceType.SLAVE)
    public String getGasAmount(String address, Long lastTimeLong) {
        // 查询所有的交易记录，统计gas
        String sql = String.format(sumGasSQL, address, lastTimeLong);
        System.out.println(sql);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        if(maps == null || maps.size() == 0){
            return "0";
        }
        Double gas = (Double) maps.get(0).get("gas");
        BigDecimal bigDecimal = new BigDecimal(gas);

        // 返回非科学计数法的值
        String plainString = bigDecimal.toPlainString();
        return plainString;

    }

    // load web3j
    public Web3j getWeb3j() {
        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        String privateKey = config.getConfig("PRIVATE_KEY_TEST", "");
        Web3j web3j = null;
        Credentials credentials = null;
        try {
            web3j = Web3j.build(new HttpService(url));
            EthBlockNumber send = web3j.ethBlockNumber().send();
            System.out.println("latest Block:" + send.getBlockNumber());
            credentials = Credentials.create(privateKey);
            System.out.println("credentials:" + credentials.getAddress());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return web3j;
    }

    // transfer value to address
//
    public synchronized String ethTransfer(String fromAddr, BigDecimal amount) {
        String url = config.getConfig("RPC", "");
        String privateKey = config.getConfig("PRIVATE_KEY_TEST", "");
        int chainid = config.getConfig("CHAINID", 198);
        Web3j web3j = getWeb3j();
        Credentials credentials = null;
        try {
            if(web3j == null){
                web3j = Web3j.build(new HttpService(url));
            }
            credentials = Credentials.create(privateKey);
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, chainid); //EIP-155
            Transfer transfer = new Transfer(web3j, transactionManager);

            BigInteger gasPrice = BigInteger.valueOf(1100000000);
            BigInteger gasLimit = BigInteger.valueOf(210000);

            RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = transfer.sendFunds(fromAddr, amount, Convert.Unit.WEI, gasPrice, gasLimit);
            TransactionReceipt transactionReceipt = transactionReceiptRemoteCall.send();
            if (transactionReceipt == null) {
                return "false";
            }
            System.out.println("transactionReceipt:" + transactionReceipt.getTransactionHash());
            System.out.println("hash:" + transactionReceipt.getTransactionHash());
            return transactionReceipt.getTransactionHash();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }

    }

    // load contract StakeBrc
    private StakeBrc loadContract(Web3j web3j, Credentials credentials) {
        String stakeAddr = config.getConfig("STAKE_CONTRACT", "");
        ContractGasProvider contractGasProvider = new StaticGasProvider(BigInteger.valueOf(210000), BigInteger.valueOf(1000000));
        StakeBrc stakeBrc = StakeBrc.load(stakeAddr, web3j, credentials, contractGasProvider);
        return stakeBrc;
    }

    // 调用stake的getStakeUser方法
    public boolean getStakeUser(String address) {
        String url = config.getConfig("RPC", "https://rpc.bitchain.biz");
        Web3j web3j = getWeb3j();
        Credentials credentials = null;
        try {
            if(web3j == null){
                web3j = Web3j.build(new HttpService(url));
            }
            String privateKey = config.getConfig("PRIVATE_KEY_TEST", "");
            credentials = Credentials.create(privateKey);
            StakeBrc stakeBrc = loadContract(web3j, credentials);
            RemoteCall<BigInteger> stakeUser = stakeBrc.getStakeUser(address);
            BigInteger send = stakeUser.send();
            System.out.println("stakeUser:" + send);
            return send.compareTo(BigInteger.ZERO) > 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
