package com.aionemu.commons.callbacks;

import com.aionemu.commons.callbacks.files.*;
import com.aionlightning.commons.callbacks.Callback;
import com.aionlightning.commons.callbacks.CallbackResult;
import com.aionlightning.commons.callbacks.EnhancedObject;
import com.aionlightning.commons.callbacks.util.GlobalCallbackHelper;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("rawtypes")
public class CallbacksTest extends Assert {

	@Test
	public void testIntResultNoCallback() {
		final MutableBoolean beforeInvoked = new MutableBoolean();
		final MutableBoolean afterInvoked = new MutableBoolean();
		int val = 10;
		TestCallbackIntObject obj = new TestCallbackIntObject(val);
		EnhancedObject eo = (EnhancedObject) obj;
		eo.addCallback(new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				beforeInvoked.setValue(true);
				return CallbackResult.newContinue();
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				afterInvoked.setValue(true);
				return CallbackResult.newContinue();
			}
		});

		int res = obj.getValue();

		assertTrue(beforeInvoked.booleanValue());
		assertTrue(afterInvoked.booleanValue());
		assertEquals(res, val);
	}

	@Test
	public void testIntResultBeforeCallback() {
		final MutableBoolean beforeInvoked = new MutableBoolean();
		final MutableBoolean afterInvoked = new MutableBoolean();
		int val = 10;
		final int newVal = 100;
		TestCallbackIntObject obj = new TestCallbackIntObject(val);
		EnhancedObject eo = (EnhancedObject) obj;
		eo.addCallback(new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				beforeInvoked.setValue(true);
				return CallbackResult.newFullBlocker(newVal);
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				afterInvoked.setValue(true);
				return CallbackResult.newContinue();
			}
		});

		int res = obj.getValue();

		assertTrue(beforeInvoked.booleanValue());
		assertTrue(afterInvoked.booleanValue());
		assertEquals(res, newVal);
	}

	@Test
	public void testGlobalStaticMehtodCallback() {
		assertEquals(TestGlobalCallbacksCaller.sayStaticHello("Hello"), "Hello");

		Callback cb = new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				return CallbackResult.newFullBlocker("Hello World");
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				return CallbackResult.newContinue();
			}
		};
		GlobalCallbackHelper.addCallback(cb);
		assertEquals(TestGlobalCallbacksCaller.sayStaticHello("Hello"), "Hello World");


		GlobalCallbackHelper.removeCallback(cb);
		assertEquals(TestGlobalCallbacksCaller.sayStaticHello("Hello"), "Hello");
	}

	@Test
	public void testGlobalMehtodCallback() {
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello");

		Callback cb = new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				return CallbackResult.newFullBlocker("Hello World");
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				return CallbackResult.newContinue();
			}
		};
		GlobalCallbackHelper.addCallback(cb);
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello World");


		GlobalCallbackHelper.removeCallback(cb);
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello");
	}

	@Test
	public void testGlobalMehtodTwoCallback() {
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello");

		Callback cb1 = new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				return CallbackResult.newFullBlocker("Hello World 1");
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				return CallbackResult.newContinue();
			}
		};

		Callback cb2 = new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				return CallbackResult.newFullBlocker("Hello World 2");
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				return CallbackResult.newContinue();
			}
		};

		GlobalCallbackHelper.addCallback(cb1);
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello World 1");

		GlobalCallbackHelper.addCallback(cb2);
		GlobalCallbackHelper.removeCallback(cb1);
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello World 2");

		GlobalCallbackHelper.removeCallback(cb2);
		assertEquals(TestGlobalCallbacksCaller.getInstance().sayHello("Hello"), "Hello");
	}

	@Test
	public void testInheritance() {
		InheritanceTestChild itc = new InheritanceTestChild();
		assertTrue(itc instanceof EnhancedObject);

		String result = "fffffff";

		((EnhancedObject) itc).addCallback(new InheritanceTestCallback(result));

		assertEquals(itc.publicMethod(), result);
	}

	@Test(timeOut = 10)
	public void testRemoveObjectCallbackFromCallback() {
		TestCallbackIntObject obj = new TestCallbackIntObject();
		final EnhancedObject eo = (EnhancedObject) obj;
		final MutableBoolean shouldBeTrue = new MutableBoolean();
		final MutableBoolean shouldNotBeTrue = new MutableBoolean();

		eo.addCallback(new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				assertEquals(eo.getCallbacks().size(), 1);
				shouldBeTrue.setValue(true);
				eo.removeCallback(this);
				return CallbackResult.newContinue();
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				shouldNotBeTrue.setValue(true);
				return CallbackResult.newContinue();
			}
		});

		obj.getValue();

		assertEquals(shouldBeTrue.booleanValue(), true);
		assertEquals(shouldNotBeTrue.booleanValue(), false);
		assertEquals(eo.getCallbacks(), null);
	}

	@Test(timeOut = 10)
	public void testRemoveGlobalCallbackFromCallback() {

		final MutableBoolean shouldBeTrue = new MutableBoolean();
		final MutableBoolean shouldNotBeTrue = new MutableBoolean();

		Callback cb = new AbstractCallback() {
			@Override
			public CallbackResult beforeCall(Object obj, Object[] args) {
				shouldBeTrue.setValue(true);
				GlobalCallbackHelper.removeCallback(this);
				return CallbackResult.newContinue();
			}

			@Override
			public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
				shouldNotBeTrue.setValue(true);
				return CallbackResult.newContinue();
			}
		};

		GlobalCallbackHelper.addCallback(cb);
		TestGlobalCallbacksCaller.getInstance().sayHello("Hello");

		assertEquals(shouldBeTrue.booleanValue(), true);
		assertEquals(shouldNotBeTrue.booleanValue(), false);
	}
}
