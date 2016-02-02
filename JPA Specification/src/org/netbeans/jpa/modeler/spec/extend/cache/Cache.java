/**
 * Copyright [2013] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jpa.modeler.spec.extend.cache;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.support.DatabaseExplorerUIs;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.modeler.core.ModelerFile;
import org.openide.util.Exceptions;

@XmlAccessorType(XmlAccessType.FIELD)
public class Cache {

    @XmlElement(name = "ct")
    private Queue<String> collectionClass;
    private final static int COLLECTION_SIZE = 5;

    @XmlElement(name = "db")
    private DatabaseConnectionCache databaseConnection;

    /**
     * @return the collectionType
     */
    public Queue<String> getCollectionClasses() {
        if (collectionClass == null) {
            collectionClass = new LinkedList<>();
            collectionClass.add(List.class.getName());
            collectionClass.add(Set.class.getName());
            collectionClass.add(Collection.class.getName());
        }
        return collectionClass;
    }

    public void addCollectionClass(String _class) {
        LinkedList<String> collection = (LinkedList) getCollectionClasses();
        if (collection.contains(_class)) {
            collection.remove(_class);
        }
        while (COLLECTION_SIZE < collection.size()) {
            collection.removeLast();
        }

        collection.addFirst(_class);

    }

    /**
     * @return the databaseConnection
     */
    public DatabaseConnectionCache getDatabaseConnection() {
        return databaseConnection;
    }

    /**
     * @param databaseConnection the databaseConnection to set
     */
    public void setDatabaseConnection(DatabaseConnectionCache databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static class DBConnectionUtil {

        /**
         * Get connection from combobox
         * @param dbConComboBox
         * @return 
         */
        public static DatabaseConnection getConnection(JComboBox dbConComboBox) {
            Object item = dbConComboBox.getSelectedItem();
            if (item instanceof DatabaseConnection) {
                return (DatabaseConnection) item;
            } else {
                return null;
            }
        }

        /**
         * Save connection from combobox
         * @param file
         * @param dbConComboBox 
         */
        public static void saveConnection(ModelerFile file, JComboBox dbConComboBox) {
            DatabaseConnection connection = DBConnectionUtil.getConnection(dbConComboBox);
            if (connection != null) {
                Cache cache = ((EntityMappings) file.getDefinitionElement()).getCache();
                DatabaseConnectionCache dbCache = new DatabaseConnectionCache();
                dbCache.setUrl(connection.getDatabaseURL());
                dbCache.setUserName(connection.getUser());
                dbCache.setPassword(connection.getPassword());
                dbCache.setDriverClassName(connection.getDriverClass());
                 try {
                        dbCache.setDriverClass(connection.getJDBCDriver().getDriver().getClass());
                    } catch (DatabaseException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                cache.setDatabaseConnection(dbCache);
            }
        }

        public static void loadConnection(ModelerFile file, JComboBox dbConComboBox) {
            loadConnection((EntityMappings) file.getDefinitionElement(), dbConComboBox);
        }
        
        /**
         * Load combobox with DB connection
         * @param entityMappings
         * @param dbConComboBox 
         */
        public static void loadConnection(EntityMappings entityMappings, JComboBox dbConComboBox) {
//            DatabaseConnection connection = DBConnectionUtil.getConnection(dbConComboBox);
            Cache cache = entityMappings.getCache();
            DatabaseConnectionCache dbCache = cache.getDatabaseConnection();

            DatabaseExplorerUIs.connect(dbConComboBox, ConnectionManager.getDefault());
            dbConComboBox.setToolTipText("Available Database Connection");

            for (int i = 0; i < dbConComboBox.getItemCount(); i++) {
                Object item = dbConComboBox.getItemAt(i);
                if (dbCache!=null && item instanceof DatabaseConnection && ((DatabaseConnection) item).getDatabaseURL().equals(dbCache.getUrl())) {
                    dbConComboBox.setSelectedIndex(i);
                    try {
                        dbCache.setDriverClass(((DatabaseConnection) item).getJDBCDriver().getDriver().getClass());
                    } catch (DatabaseException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                }
            }
        }
    }

}