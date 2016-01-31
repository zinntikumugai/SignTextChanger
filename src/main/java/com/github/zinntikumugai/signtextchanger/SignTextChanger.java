/**
 * メイン
 */
package com.github.zinntikumugai.signtextchanger;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SignTextChanger extends JavaPlugin implements Listener {

	private static SignTextChanger instance;
	//設置した看板
	HashMap<Player, Integer> SignChangex = new HashMap<Player, Integer>();
	HashMap<Player, Integer> SignChangey = new HashMap<Player, Integer>();
	HashMap<Player, Integer> SignChangez = new HashMap<Player, Integer>();
	//クリックした看板
	HashMap<Player, Integer> ClickSignx = new HashMap<Player, Integer>();
	HashMap<Player, Integer> ClickSigny = new HashMap<Player, Integer>();
	HashMap<Player, Integer> ClickSignz = new HashMap<Player, Integer>();
	//クリックした時の時間
	HashMap<Player, Long> ClickTime = new HashMap<Player, Long>();
	//看板を設置した時間
	HashMap<Player, Long> PlaceSignTime = new HashMap<Player, Long>();

	HashMap<Player, Block> ClickBlock = new HashMap<Player,Block>();

	//看板を設置した時ボーン
	@EventHandler
	public void onSignCange(SignChangeEvent event){
		if(event.getBlock().getType().equals(Material.SIGN) | event.getBlock().getType().equals(Material.SIGN_POST) | event.getBlock().getType().equals(Material.WALL_SIGN)){
			Player player = event.getPlayer();
			SignChangex.put(player, event.getBlock().getX());
			SignChangey.put(player, event.getBlock().getY());
			SignChangez.put(player, event.getBlock().getZ());
			PlaceSignTime.put(player, player.getPlayerTime());
			System.out.println("看板を書き込みました");
		}
	}


	//コマンド実行時実行
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(command.getName().equalsIgnoreCase("sct")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if((ClickSignx.get(player) != null)) {
					if((ClickSigny.get(player) != null)) {
						if((ClickSignz.get(player) != null)) {
							//看板をクリックしている
							if(PlaceSignTime.get(player) != null){
								//看板を置いている↓エラー対策
								if( ((PlaceSignTime.get(player)/20) >0) && (PlaceSignTime.get(player)/20-(ClickTime.get(player)/20) <= 3600) ) {
									//看板を置いてから時間が指定以内の時
									if( (ClickTime.get(player)/20 > 0) && ((ClickTime.get(player)/20-(PlaceSignTime.get(player)/20)) <= 120) ){
										//クリックしてから時間が指定以内の時
										if(args.length >= 2) {
											int i = Integer.parseInt(args[0]);
											if( (i >= 0 ) && ( i <= 3 ) ) {
												//System.out.println("行数おｋ");
												if(!(args[1] == null)) {
													//System.out.println("書き込みますか");
													Block block = ClickBlock.get(player);
													Sign sign = (Sign)block.getState();
													String temptemp = "";
													temptemp = changeand(args[1]);
													//System.out.println("変換後の文字: " + temptemp);
													sign.setLine(i, temptemp);
													//sign.setLine(i, args[1]);
													sign.update();
													//System.out.println("書き込みました");
												}else{
													//System.out.println("書き込めないだと!?!?!?");
												}
											}else{
												player.sendMessage("§c看板の行数っていくつだっけ(´・ω・｀)");
											}
										}
									}else{
										player.sendMessage("§cクリックから時間が経ちすぎています");
									}
								}else{
									player.sendMessage("§c設置から時間が経ちすぎています");
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	/**
	 * Bukkitのカラーコードの表記("&1","&d")の'&'を'§'に変更する<br>
	 * 対応しているもののみ変更するので単なる'&'は変更しない
	 * @param text		変更前の文字('&'表記)
	 * @return			変更後の文字('§'表記)
	 */
	public static String changeand(String text) {
		String temp = "";
		//System.out.println(text);
		for(int s=0;s<text.length()-1;s++) {
			//System.out.println(text.substring(s,s+1));
			//"&"の後にa-s or 0-9の時"§"に変更(色対応)
			if(text.substring(s,s+2).matches("&[a-z0-9]")) {
				//System.out.println("マッチしたぜ");
				temp += "§";
				temp += text.substring(s+1,s+2);
				s++;
			}else {
				//System.out.println("マッチして何の");
				temp += text.substring(s,s+1);
			}
			//System.out.println(text.length()-s);
			//System.out.println(temp);
			if(text.length()-s == 2) {
		temp += text.substring(text.length()-1);
			}
		}
		//System.out.println("最後の文字: " + text.substring(text.length()-1));
		return temp;
	}


	//看板を右クリックしたら実行
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			//右クリックしたものがブロックだったら
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN) | event.getClickedBlock().getType().equals(Material.SIGN_POST)){
				//クリックしたブロックが立ち看板or張り付き看板だったら
				//"|"一つだとそれより後ろに書かれたものも調べるが2つにすると調べない
				//今回は別々のもので調べる必要があるので一つ
				Player player = event.getPlayer();
				if( (SignChangex.get(player) != null) && (SignChangex.get(player).equals(event.getClickedBlock().getX()))  ){
					if( (SignChangey.get(player) != null) && (SignChangey.get(player).equals(event.getClickedBlock().getY())) ){
						if( (SignChangez.get(player) != null) && (SignChangez.get(player).equals(event.getClickedBlock().getZ()))){
							//すでに設置してある看板をクリックしていてかつ以前にクリックしていなかったら
							player.sendMessage("この看板を選択肢ました!!");
							ClickSignx.put(player, event.getClickedBlock().getX());
							ClickSigny.put(player, event.getClickedBlock().getY());
							ClickSignz.put(player, event.getClickedBlock().getZ());
							ClickTime.put(player, player.getPlayerTime());
							ClickBlock.put(player, event.getClickedBlock());
						}
					}
				}
			}
		}
	}

	static void noSignClick(Player player) {
		player.sendMessage("設置してからクリックしてください");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		ClickSignx.put(player, null);
		ClickSigny.put(player, null);
		ClickSignz.put(player, null);
		ClickTime.put(player, null);
		PlaceSignTime.put(player, null);
		SignChangex.put(player, null);
		SignChangey.put(player, null);
		SignChangez.put(player, null);
	}


	//プラグインが読み込まれると実行
	@Override
	public void onEnable(){
		//コンフィグ読み取り なければコピー
		File configFile = new File(SignTextChanger.getInstatnce().getDataFolder(),"config.yml");
		if(!configFile.exists())
			this.saveDefaultConfig();

		getServer().getPluginManager().registerEvents(this,this);
	}

	//プラグインが閉じられると実行
	@Override
	public void onDisable() {
	}

	/**
	 * SiginTextChangerのインスタンスを返す
	 * @return SiginTextChanger
	 */
	public static SignTextChanger getInstatnce(){
		if(instance == null) {
			instance = (SignTextChanger)Bukkit.getPluginManager().getPlugin("SignTextChanger");
		}
		return instance;
	}

}
