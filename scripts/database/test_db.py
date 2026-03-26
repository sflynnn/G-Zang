#!/usr/bin/env python3
import mysql.connector
from mysql.connector import Error

try:
    connection = mysql.connector.connect(
        host='localhost',
        user='root',
        password='123456',
        database='g_zang'
    )
    if connection.is_connected():
        print('Connected to g_zang database successfully')

        cursor = connection.cursor()
        cursor.execute('SHOW TABLES')
        tables = cursor.fetchall()
        print(f'Found {len(tables)} tables:')
        for table in tables:
            print(f'  - {table[0]}')
        cursor.close()
        connection.close()
    else:
        print('Connection failed')
except Error as e:
    print(f'Database connection error: {e}')
