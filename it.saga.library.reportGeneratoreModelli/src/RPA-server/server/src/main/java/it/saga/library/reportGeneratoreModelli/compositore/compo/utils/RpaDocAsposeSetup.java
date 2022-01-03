package it.saga.library.reportGeneratoreModelli.compositore.compo.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RpaDocAsposeSetup {
	public static void apply() throws Exception {
		Class<?> ZOE = Class.forName("com.aspose.words.zzZOE");
		Field zzZ7J = ZOE.getDeclaredField("zzZ7J");
		zzZ7J.setAccessible(true);
		Field zzZ7K = ZOE.getDeclaredField("zzZ7K");
		zzZ7K.setAccessible(true);
		final String MODIFIERS_FIELD = "modifiers";
		Field modifiersField = Field.class.getDeclaredField(MODIFIERS_FIELD);
		modifiersField.setAccessible(true);
		int modifiers = modifiersField.getInt(zzZ7K);
		modifiers &= ~Modifier.FINAL;
		modifiersField.setInt(zzZ7K, modifiers);
		zzZ7K.set(null, zzZ7J.get(null));
	}
}