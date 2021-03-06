package org.molgenis.ui.genenetwork.matrix.impl;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.molgenis.data.MolgenisDataException;
import org.ujmp.core.Matrix;

import java.io.File;
import java.io.IOException;

import static org.ujmp.core.Matrix.Factory;

class DoubleMatrix
{
	private final File file;
	private final char separator;

	private TObjectIntHashMap columnMap = new TObjectIntHashMap();
	private TObjectIntHashMap rowMap = new TObjectIntHashMap();
	private Matrix matrix;

	private boolean inited = false;

	public DoubleMatrix(File file, char separator)
	{
		this.file = file;
		this.separator = separator;
	}

	private void init()
	{
		try
		{
			matrix = Factory.linkTo().file(file.getAbsolutePath()).asDenseCSV(separator);
			setRowIndicesMap();
			setColumnIndicesMap();
		}
		catch (IOException e)
		{
			throw new MolgenisDataException(e);
		}
		inited = true;
	}

	public double getValueByIndex(int row, int column)
	{
		if (!inited) init();
		if (row > matrix.getRowCount()) throw new IndexOutOfBoundsException(
				"Index [" + row + "] is greater than the number of columns in the matrix [" + matrix.getRowCount()
						+ "]");
		if (column > matrix.getColumnCount()) throw new IndexOutOfBoundsException(
				"Index [" + column + "] is greater than the number of columns in the matrix [" + matrix.getColumnCount()
						+ "]");
		Double result = matrix.getAsDouble(row, column);
		if (result.isNaN())
		{
			throw new MolgenisDataException("No score found");
		}
		return result;
	}

	public double getValueByName(String row, String column)
	{
		if (!inited) init();
		Integer rowIndex = rowMap.get(row);
		Integer columnIndex = columnMap.get(column);
		if (rowIndex == null) throw new MolgenisDataException("Unknown row name [" + row + "]");
		if (columnIndex == null) throw new MolgenisDataException("Unknown column name [" + column + "]");
		return getValueByIndex(rowIndex, columnIndex);
	}

	private void setRowIndicesMap()
	{
		int i = 0;
		String gene;
		while (i < matrix.getRowCount())
		{
			gene = matrix.getAsString(i, 0);
			rowMap.put(gene, i);
			i++;
		}
	}

	private void setColumnIndicesMap()
	{
		int i = 0;
		String hpo;
		while (i < matrix.getColumnCount())
		{
			hpo = matrix.getAsString(0, i);
			columnMap.put(hpo, i);
			i++;
		}
	}
}
