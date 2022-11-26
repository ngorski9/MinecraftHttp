package minecrafthttp;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class SynchronousSender implements ConsoleCommandSender{

	private ConsoleCommandSender s;
	private Plugin p;
	private String log;
	
	public SynchronousSender(Plugin p) {
		s = Bukkit.getConsoleSender();
		this.p = p;
	}
	
	public String captureCommand(String command) {
		log = "";
		try {
			Bukkit.getScheduler().callSyncMethod( p, () -> Bukkit.dispatchCommand( this, command ) ).get();
		}
		catch(Exception e){
			return "DISPATCH ERROR";
		}

		return log;
	}
	
	public void synchronousCommand(String command) {
		try {
			Bukkit.getScheduler().callSyncMethod( p, () -> Bukkit.dispatchCommand( this, command ) ).get();
		}
		catch(Exception e){
			
		}
	}
	
	// send message overrides
	
	@Override
	public void sendMessage(String arg0) {
		log = arg0;
	}

	@Override
	public void sendMessage(String... arg0) {
		log = arg0[0];
	}

	@Override
	public void sendMessage(UUID arg0, String arg1) {
		log = arg1;
	}

	@Override
	public void sendMessage(UUID arg0, String... arg1) {
		log = arg1[1];
	}
	
	@Override
	public void sendRawMessage(String arg0) {
		s.sendRawMessage(arg0);
	}

	@Override
	public void sendRawMessage(UUID arg0, String arg1) {
		s.sendRawMessage(arg0, arg1);
	}
	
	// everything below here is just overrides
	
	
	@Override
	public String getName() {
		return s.getName();
	}

	@Override
	public Server getServer() {
		return s.getServer();
	}

	@Override
	public Spigot spigot() {
		return s.spigot();
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return s.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return s.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return s.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return s.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return s.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return s.hasPermission(arg0);
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return s.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return s.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return s.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		s.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		s.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return s.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		s.setOp(arg0);
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		s.abandonConversation(arg0);
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
		s.abandonConversation(arg0, arg1);
	}

	@Override
	public void acceptConversationInput(String arg0) {
		s.acceptConversationInput(arg0);
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		return s.beginConversation(arg0);
	}

	@Override
	public boolean isConversing() {
		return s.isConversing();
	}
}
