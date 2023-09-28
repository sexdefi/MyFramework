// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/token/ERC20/utils/SafeERC20.sol";

contract StakeReturnGas is ERC20,Ownable {
    using SafeERC20 for IERC20;

    string _name = "GAS Ticket";
    string _symbol = "GAS_Ticket";

    constructor() ERC20(_name,_symbol){}
    address public _BRC = 0xF89c8c3cf0D39745f9F691fc2839572dDc00e02f;
    uint256 public quota = 100 ether;
    bool public paused = true;
    mapping(address => User) public users;
    address[] userArray;

    bytes32 public saleMerkleRoot;

    struct User {
        address Wallet;
        uint256 StakeAmount;
        uint256 ExtractTime;
    }

    modifier callerIsUser() {
        require(tx.origin == msg.sender, "The caller is another contract");
        _;
    }

    function stake()
    external
    callerIsUser
    returns (bool)
    {
        require(paused == false, "Activity is paused");
        User memory u = users[msg.sender];
        u.Wallet = msg.sender;
        u.StakeAmount = quota;
        u.ExtractTime = 0;
        if(users[msg.sender].Wallet == address(0)){
            userArray.push(msg.sender);
        }
        users[msg.sender] = u;
        IERC20(_BRC).safeTransferFrom(
            msg.sender,
            address(this),
            quota
        );
        _mint(msg.sender,quota / 100);
        return true;
    }

    function withdrawToken() external callerIsUser returns (bool){
        require(users[msg.sender].StakeAmount > 0, "sender stake amount is 0");
        require(balanceOf(msg.sender) > 0, "not enough gas ticket");
        _burn(msg.sender,balanceOf(msg.sender));

        IERC20(_BRC).safeTransfer(
            msg.sender,
            users[msg.sender].StakeAmount
        );
        users[msg.sender].StakeAmount = 0;
        users[msg.sender].ExtractTime = block.timestamp;
        return true;
    }

    function setAddress(address _brc) public onlyOwner {
        _BRC = _brc;
    }

    function setNumbers(uint256 _quota) public onlyOwner {
        quota = _quota;
    }

    function setPaused(bool _paused) external onlyOwner {
        require(paused != _paused);
        paused = _paused;
    }

    function getUserCount() external view returns(uint256){
        return userArray.length;
    }

    function getStakeListPage(uint256 pageNo,uint256 pageSize) external view returns(User[] memory){
        User[] memory _users = new User[](pageSize);
        uint256 total = userArray.length;
        uint256 start = pageNo * pageSize;
        uint256 end = start + pageSize - 1;
        for ( uint256 i = start; i < end; i ++ ) {
            if(i < total) {
                User memory u = users[userArray[i]];
                _users[i - start] = u;
            }
        }
        return _users;
    }

    function getStakeUser(address _addr) public view returns(uint256 amount){
        return users[_addr].StakeAmount;
    }


    // function getStakeList(uint256 start, uint256 end)
    //     external
    //     view
    //     returns (User[] memory)
    // {
    //     require(start <= end && end <= userArray.length - 1);
    //     User[] memory _users = new User[](end - start + 1);
    //     uint256 i = 0;
    //     for (start; start <= end; start++) {
    //         User memory u;
    //         u = users[userArray[i]];

    //         _users[i] = u;
    //         i++;
    //     }
    //     return _users;
    // }

    receive() external payable {}

    fallback() external payable {
        require(msg.data.length == 0);
    }

    function withdrawToken(address[] calldata tokenAddr, address recipient)
    public
    onlyOwner
    {
        {
            uint256 ethers = address(this).balance;
            if (ethers > 0) payable(recipient).transfer(ethers);
        }
    unchecked {
        for (uint256 index = 0; index < tokenAddr.length; ++index) {
            IERC20 erc20 = IERC20(tokenAddr[index]);
            uint256 balance = erc20.balanceOf(address(this));
            if (balance > 0) erc20.transfer(recipient, balance);
        }
    }
    }

    function _transfer(address sender, address recipient, uint256 amount) override internal
    {
        revert("cannot transfer");
    }
}
