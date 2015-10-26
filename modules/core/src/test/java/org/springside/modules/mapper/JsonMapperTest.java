package org.springside.modules.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 测试Jackson对Object,Map,List,数组,枚举,日期类等的持久化.
 * 更多测试见showcase中的JsonDemo.
 * 
 * @author calvin
 */
public class JsonMapperTest {

	private static JsonMapper binder = JsonMapper.nonDefaultMapper();

	/**
	 * 序列化对象/集合到Json字符串.
	 */
	@Test
	public void toJson() throws Exception {
		//Bean
		TestBean bean = new TestBean("A");
		String beanString = binder.toJson(bean);
		System.out.println("Bean:" + beanString);
		assertEquals("{\"name\":\"A\"}", beanString);

		//Map
		Map<String, Object> map = Maps.newLinkedHashMap();
		map.put("name", "A");
		map.put("age", 2);
		String mapString = binder.toJson(map);
		System.out.println("Map:" + mapString);
		assertEquals("{\"name\":\"A\",\"age\":2}", mapString);

		//List<String>
		List<String> stringList = Lists.newArrayList("A", "B", "C");
		String listString = binder.toJson(stringList);
		System.out.println("String List:" + listString);
		assertEquals("[\"A\",\"B\",\"C\"]", listString);

		//List<Bean>
		List<TestBean> beanList = Lists.newArrayList(new TestBean("A"), new TestBean("B"));
		String beanListString = binder.toJson(beanList);
		System.out.println("Bean List:" + beanListString);
		assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanListString);

		//Bean[]
		TestBean[] beanArray = new TestBean[] { new TestBean("A"), new TestBean("B") };
		String beanArrayString = binder.toJson(beanArray);
		System.out.println("Array List:" + beanArrayString);
		assertEquals("[{\"name\":\"A\"},{\"name\":\"B\"}]", beanArrayString);
	}

	/**
	 * 从Json字符串反序列化对象/集合.
	 */
	@Test
	public void fromJson() throws Exception {
		//Bean
		String beanString = "{\"name\":\"A\"}";
		TestBean bean = binder.fromJson(beanString, TestBean.class);
		System.out.println("Bean:" + bean);

		//Map
		String mapString = "{\"name\":\"A\",\"age\":2}";
		Map<String, Object> map = binder.fromJson(mapString, HashMap.class);
		System.out.println("Map:");
		for (Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

		//List<String>
		String listString = "[\"A\",\"B\",\"C\"]";
		List<String> stringList = binder.getMapper().readValue(listString, List.class);
		System.out.println("String List:");
		for (String element : stringList) {
			System.out.println(element);
		}

		//List<Bean>
		String beanListString = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
		List<TestBean> beanList = binder.getMapper().readValue(beanListString, new TypeReference<List<TestBean>>() {
		});
		System.out.println("Bean List:");
		for (TestBean element : beanList) {
			System.out.println(element);
		}
		
		
		//复杂类型Bean
		JsonMapper newMapper = new JsonMapper();
		SimpleModule salareModule = new SimpleModule("SalaresModel");
		salareModule.addSerializer(new SalareSerializer());
		salareModule.addDeserializer(Salare.class, new SalareDeserializer());
		newMapper.getMapper().registerModule(salareModule);
		
		User u = new User();
		u.setName("Lily");
		List<Salare> s = new ArrayList<Salare>();
		s.add(new Salare("月薪",50000.0));
		s.add(new Salare("月奖金",10000.0));
		s.add(new Salare("全勤",10000.0));
		u.setSalares(s);
		
		String json = newMapper.toJson(u);
		System.out.println(json);
		
		u = newMapper.fromJson(json, User.class);
		System.out.println(u.getSalares().size());
		
	}
	
	public static class User{
		private String name;
		private List<Salare> salares;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<Salare> getSalares() {
			return salares;
		}
		public void setSalares(List<Salare> salares) {
			this.salares = salares;
		}
	}
	public static class Salare{
		private String name;
		private Double value;
		public Salare() {
		}
		public Salare(String name, Double value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Double getValue() {
			return value;
		}
		public void setValue(Double value) {
			this.value = value;
		}
		
	}
	
	public static class SalareSerializer extends StdSerializer<Salare>{
		public SalareSerializer() {
			super(Salare.class);
		}
		public void serialize(Salare value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			JsonMapper newMapper = new JsonMapper();
			jgen.writeString(newMapper.toJson(value));
		}
	}
	
	public static class SalareDeserializer extends StdDeserializer<Salare> {
		public SalareDeserializer() {
			super(Salare.class);
		}

		@Override
		public Salare deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			JsonMapper newMapper = new JsonMapper();
			return newMapper.fromJson(jp.getText(), Salare.class);
		}
	}
	/**
	 * 测试传入空对象,空字符串,Empty的集合,"null"字符串的结果.
	 */
	@Test
	public void nullAndEmpty() {
		// toJson测试 //

		//Null Bean
		TestBean nullBean = null;
		String nullBeanString = binder.toJson(nullBean);
		assertEquals("null", nullBeanString);

		//Empty List
		List<String> emptyList = Lists.newArrayList();
		String emptyListString = binder.toJson(emptyList);
		assertEquals("[]", emptyListString);

		// fromJson测试 //

		//Null String for Bean
		TestBean nullBeanResult = binder.fromJson(null, TestBean.class);
		assertNull(nullBeanResult);

		nullBeanResult = binder.fromJson("null", TestBean.class);
		assertNull(nullBeanResult);

		//Null/Empty String for List
		List nullListResult = binder.fromJson(null, List.class);
		assertNull(nullListResult);

		nullListResult = binder.fromJson("null", List.class);
		assertNull(nullListResult);

		nullListResult = binder.fromJson("[]", List.class);
		assertEquals(0, nullListResult.size());
	}

	/**
	 * 测试三种不同的Binder.
	 */
	@Test
	public void threeTypeBinders() {
		//打印全部属性
		JsonMapper normalBinder = new JsonMapper();
		TestBean bean = new TestBean("A");
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\",\"nullValue\":null}", normalBinder.toJson(bean));

		//不打印nullValue属性
		JsonMapper nonNullBinder = JsonMapper.nonEmptyMapper();
		assertEquals("{\"name\":\"A\",\"defaultValue\":\"hello\"}", nonNullBinder.toJson(bean));

		//不打印默认值未改变的nullValue与defaultValue属性
		JsonMapper nonDefaultBinder = JsonMapper.nonDefaultMapper();
		assertEquals("{\"name\":\"A\"}", nonDefaultBinder.toJson(bean));
	}

	public static class TestBean {

		private String name;
		private String defaultValue = "hello";
		private String nullValue = null;

		public TestBean() {
		}

		public TestBean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public String getNullValue() {
			return nullValue;
		}

		public void setNullValue(String nullValue) {
			this.nullValue = nullValue;
		}

		@Override
		public String toString() {
			return "TestBean [defaultValue=" + defaultValue + ", name=" + name + ", nullValue=" + nullValue + "]";
		}
	}

}
