package com.energygames.lojadegames.converter;

import com.energygames.lojadegames.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA para persistir RoleEnum como INTEGER no banco de dados.
 * 
 * Este conversor:
 * - Salva o código numérico da role (1, 2, 3...) ao invés de strings
 * - Reduz espaço em disco (4 bytes vs ~10-15 bytes)
 * - Melhora performance de queries e comparações
 * - Facilita adição de novas roles sem ALTER TABLE
 * 
 * Uso automático: @Convert(converter = RoleEnumConverter.class)
 * 
 * @see RoleEnum
 */
@Converter(autoApply = false)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, Integer> {

	/**
	 * Converte RoleEnum para Integer ao persistir no banco.
	 * 
	 * @param attribute RoleEnum a ser convertido
	 * @return Código inteiro da role, ou null se attribute for null
	 */
	@Override
	public Integer convertToDatabaseColumn(RoleEnum attribute) {
		if (attribute == null) {
			return null;
		}
		return attribute.getCode();
	}

	/**
	 * Converte Integer do banco para RoleEnum ao ler.
	 * 
	 * @param dbData Código inteiro do banco de dados
	 * @return RoleEnum correspondente, ou null se dbData for null
	 * @throws IllegalArgumentException se código não existir
	 */
	@Override
	public RoleEnum convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return RoleEnum.fromCode(dbData);
	}
}
