/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/
package org.apache.cayenne.query;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.test.jdbc.DBHelper;
import org.apache.cayenne.test.jdbc.TableHelper;
import org.apache.cayenne.testdo.testmap.Artist;
import org.apache.cayenne.unit.di.server.ServerCase;
import org.apache.cayenne.unit.di.server.UseServerRuntime;
import org.junit.Test;

@UseServerRuntime(ServerCase.TESTMAP_PROJECT)
public class ObjectSelect_RunIT extends ServerCase {

	@Inject
	private DataContext context;

	@Inject
	private DBHelper dbHelper;

	@Override
	protected void setUpAfterInjection() throws Exception {
		dbHelper.deleteAll("PAINTING_INFO");
		dbHelper.deleteAll("PAINTING");
		dbHelper.deleteAll("ARTIST_EXHIBIT");
		dbHelper.deleteAll("ARTIST_GROUP");
		dbHelper.deleteAll("ARTIST");
	}

	protected void createArtistsDataSet() throws Exception {
		TableHelper tArtist = new TableHelper(dbHelper, "ARTIST");
		tArtist.setColumns("ARTIST_ID", "ARTIST_NAME", "DATE_OF_BIRTH");

		long dateBase = System.currentTimeMillis();

		for (int i = 1; i <= 20; i++) {
			tArtist.insert(i, "artist" + i, new java.sql.Date(dateBase + 10000 * i));
		}
	}

	@Test
	public void test_SelectObjects() throws Exception {

		createArtistsDataSet();

		List<Artist> result = ObjectSelect.query(Artist.class).select(context);
		assertEquals(20, result.size());
		assertThat(result.get(0), instanceOf(Artist.class));

		Artist a = ObjectSelect.query(Artist.class).exp(Artist.ARTIST_NAME.eq("artist14")).selectOne(context);
		assertNotNull(a);
		assertEquals("artist14", a.getArtistName());
	}

	@Test
	public void test_SelectDataRows() throws Exception {

		createArtistsDataSet();

		List<DataRow> result = ObjectSelect.dataRowQuery(Artist.class).select(context);
		assertEquals(20, result.size());
		assertThat(result.get(0), instanceOf(DataRow.class));

		DataRow a = ObjectSelect.dataRowQuery(Artist.class).exp(Artist.ARTIST_NAME.eq("artist14")).selectOne(context);
		assertNotNull(a);
		assertEquals("artist14", a.get("ARTIST_NAME"));
	}
}
