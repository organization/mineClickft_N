package mineclickft;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWool;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class main extends PluginBase implements Listener {
	public HashMap<String, Object> coins = null;
	public String pix = "§6§l[ MineClickft ] §r";
	private static main instance;


	public static main getInstance() {
		if (instance == null) {
			instance = new main();
		}
		return instance;
	}

	public main() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getDataFolder().mkdirs();
		this.coins = this.getCoinAll(new Config(new File(this.getDataFolder(), "coins.json"), Config.JSON));
	}

	@Override
	public void onDisable() {
		this.save();
	}


	/*
	 * 해당 문자열이 숫자로 가능한지 하는 매서드입니다
	 * 
	 * @param str
	 */
	public boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException|NullPointerException e) {
			return false;
		}
		return true;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!this.coins.containsKey(event.getPlayer().getName().toLowerCase())) {
			this.coins.put(event.getPlayer().getName().toLowerCase(), 1);
		}
		event.setJoinMessage(TextFormat.colorize("&f[&a+&f] &e" + event.getPlayer().getName() + "&7님이 게임에 참여했습니다"));

	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {

		if (event.getBlock().getId() == Block.WOOL) {
			int meta = this.rand(0, 16);
			/* 양털색변경 */
			if (meta <= 15) {
				event.getBlock().getLevel().setBlock(event.getBlock(), new BlockWool(meta));
				return;
				/* 옵시디언으로 변경 */
			}
			event.getBlock().getLevel().setBlock(event.getBlock(), Block.get(Block.GLOWING_OBSIDIAN));
			/* 옵시디언 터치시 */
		} else if (event.getBlock().getId() == Block.GLOWING_OBSIDIAN) {
			this.addCoin(event.getPlayer().getName(), 1);
			event.getBlock().getLevel().setBlock(event.getBlock(), Block.get(Block.WOOL));
		}
	}

	public void addCoin(String name, int index) {
		if (this.coins.containsKey(name.toLowerCase())) {
			this.coins.replace(name.toLowerCase(), (int) this.coins.get(name.toLowerCase()) + index);
			if (this.getServer().getPlayer(name).isOnline()) {
				this.getServer().getPlayerExact(name)
						.sendMessage(TextFormat.colorize("&6&l[ MineClickft ] &r&eYOU GET &7["
								+ this.coins.get(name.toLowerCase()) + "]&c&l + " + index + "Coin"));
			}
		}
	}

	public boolean reduceCoin(String name, int index) {
		if (this.coins.containsKey(name.toLowerCase())) {
			if (((int) this.coins.get(name.toLowerCase())) - index >= 0) {
				this.coins.replace(name.toLowerCase(), (int) this.coins.get(name.toLowerCase()) - index);
				return true;
			}
			return false;
		}
		return false;
	}

	public int rand(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	public void save() {
		Config config = new Config(new File(this.getDataFolder(), "coins.json"), Config.JSON);
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		this.coins.forEach((String, Object) -> {
			map.put(String, (int) Object);
		});
		config.setAll(map);
		config.save();
	}

	public HashMap<String, Object> getCoinAll(Config config) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		config.getAll().forEach((String, Object) -> {
			if (Object.getClass() == Double.class) {
				map.put(String, (int) Integer.parseInt(Object.toString().replace(".0", "")));

			} else {
				map.put(String, ((int) Object));
			}
		});
		return map;
	}
	@EventHandler
	public void onb(PlayerInvalidMoveEvent e) {

		e.setCancelled();
	}
}
