package jp.ats.liverwort.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jp.ats.liverwort.jdbc.OptionKey;

/**
 * キー、値ともに String のオプションマップから値を取り出し、変換することが可能なオプションキークラスです。
 *
 * @author 千葉 哲嗣
 *
 * @param <T> オプション値の型
 */
public class ParsableOptionKey<T> extends OptionKey<T> {

	enum OptionValueParser {

		TO_STRING {

			@Override
			@SuppressWarnings("unchecked")
			<T> T parse(String value) {
				return (T) value;
			}
		},

		TO_STRING_ARRAY {

			@Override
			@SuppressWarnings("unchecked")
			<T> T parse(String value) {
				return (T) value.split(" +");
			}
		},

		TO_BOOLEAN {

			@Override
			@SuppressWarnings("unchecked")
			<T> T parse(String value) {
				return (T) Boolean.valueOf(value);
			}
		},

		TO_CLASS {

			@Override
			@SuppressWarnings("unchecked")
			<T> T parse(String value) {
				try {
					return (T) Class.forName(value);
				} catch (Exception e) {
					throw new IllegalStateException();
				}
			}
		};

		abstract <T> T parse(String value);
	}

	private static final Object lock = new Object();

	private static final Map<String, ParsableOptionKey<?>> map = new HashMap<>();

	private final OptionValueParser parser;

	/**
	 * 文字列のキーからこのクラスのインスタンスに変換します。
	 *
	 * @param <T> オプション値の型
	 * @param key 変換前のキー
	 * @return 変換後のキー
	 */
	@SuppressWarnings("unchecked")
	public static <T> ParsableOptionKey<T> convert(String key) {
		synchronized (lock) {
			return (ParsableOptionKey<T>) map.get(key);
		}
	}

	ParsableOptionKey(String key, OptionValueParser parser) {
		synchronized (lock) {
			map.put(key, this);
		}

		this.parser = parser;
	}

	/**
	 * 値を変換します。
	 *
	 * @param value 変換前の値
	 * @return 返還後の値
	 */
	public Optional<T> parse(String value) {
		return Optional.ofNullable(value).map(v -> parser.parse(v));
	}
}
