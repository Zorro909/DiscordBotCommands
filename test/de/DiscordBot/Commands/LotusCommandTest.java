package de.DiscordBot.Commands;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.impl.MessageImpl;

public class LotusCommandTest {

	@Test
	public void test() {
		LotusCommand lc = new LotusCommand();
		Object o = lc.execute(null, null, null);
		Assert.assertEquals(o.getClass(), MessageImpl.class);
		if(!((Message) o).getContent().equalsIgnoreCase("~help")) {
			fail("LotusCommand should post ~help, but it doesn't!");
		}
	}

}
