#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
G-Zang 数据库读取脚本
用于连接本地MySQL数据库并读取g_zang数据库中的所有表
"""

import mysql.connector
from mysql.connector import Error
import pandas as pd
from tabulate import tabulate
import sys


class GZangDBReader:
    """G-Zang数据库读取器"""

    def __init__(self, host='localhost', user='root', password='123456', database='g_zang'):
        """
        初始化数据库连接参数

        Args:
            host (str): 数据库主机地址
            user (str): 用户名
            password (str): 密码
            database (str): 数据库名
        """
        self.host = host
        self.user = user
        self.password = password
        self.database = database
        self.connection = None

    def connect(self):
        """建立数据库连接"""
        try:
            self.connection = mysql.connector.connect(
                host=self.host,
                user=self.user,
                password=self.password,
                database=self.database
            )
            if self.connection.is_connected():
                print(f"✅ 成功连接到 {self.database} 数据库")
                return True
        except Error as e:
            print(f"❌ 连接数据库失败: {e}")
            return False

    def disconnect(self):
        """断开数据库连接"""
        if self.connection and self.connection.is_connected():
            self.connection.close()
            print("✅ 数据库连接已关闭")

    def get_table_names(self):
        """获取数据库中的所有表名"""
        try:
            cursor = self.connection.cursor()
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            cursor.close()
            return [table[0] for table in tables]
        except Error as e:
            print(f"❌ 获取表名失败: {e}")
            return []

    def read_table(self, table_name, limit=100):
        """
        读取指定表的内容

        Args:
            table_name (str): 表名
            limit (int): 限制返回的行数，默认100行
        """
        try:
            cursor = self.connection.cursor()
            cursor.execute(f"SELECT * FROM {table_name} LIMIT {limit}")

            # 获取列名
            column_names = [desc[0] for desc in cursor.description]

            # 获取数据
            rows = cursor.fetchall()
            cursor.close()

            return column_names, rows
        except Error as e:
            print(f"❌ 读取表 {table_name} 失败: {e}")
            return None, None

    def get_table_info(self, table_name):
        """获取表的详细信息"""
        try:
            cursor = self.connection.cursor()

            # 获取表结构
            cursor.execute(f"DESCRIBE {table_name}")
            columns = cursor.fetchall()

            # 获取表记录数
            cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            count = cursor.fetchone()[0]

            cursor.close()

            return columns, count
        except Error as e:
            print(f"❌ 获取表 {table_name} 信息失败: {e}")
            return None, 0

    def display_table(self, table_name, limit=10):
        """
        显示表的预览信息

        Args:
            table_name (str): 表名
            limit (int): 显示的行数限制
        """
        print(f"\n{'='*60}")
        print(f"📋 表: {table_name}")
        print(f"{'='*60}")

        # 获取表信息
        columns, count = self.get_table_info(table_name)
        if columns:
            print(f"📊 总记录数: {count}")
            print("🏗️  表结构:")
            print(tabulate(columns,
                          headers=['字段名', '类型', '可空', '键', '默认值', '额外'],
                          tablefmt='grid'))

        # 读取表数据
        column_names, rows = self.read_table(table_name, limit)
        if column_names and rows:
            print(f"\n📝 数据预览 (前{limit}行):")
            if len(rows) > 0:
                print(tabulate(rows, headers=column_names, tablefmt='grid'))
            else:
                print("该表暂无数据")
        elif column_names and not rows:
            print("该表暂无数据")
        print()

    def export_to_csv(self, table_name, filename=None):
        """
        将表导出为CSV文件

        Args:
            table_name (str): 表名
            filename (str): 输出文件名，如果不指定则使用表名
        """
        if not filename:
            filename = f"{table_name}.csv"

        try:
            cursor = self.connection.cursor()
            cursor.execute(f"SELECT * FROM {table_name}")

            # 获取列名
            column_names = [desc[0] for desc in cursor.description]

            # 获取所有数据
            rows = cursor.fetchall()
            cursor.close()

            # 创建DataFrame并导出
            df = pd.DataFrame(rows, columns=column_names)
            df.to_csv(filename, index=False, encoding='utf-8-sig')
            print(f"✅ 表 {table_name} 已导出到 {filename}")
            return True
        except Error as e:
            print(f"❌ 导出表 {table_name} 失败: {e}")
            return False


def main():
    """主函数"""
    print("🚀 G-Zang 数据库读取工具")
    print("=" * 50)

    # 创建数据库读取器
    db_reader = GZangDBReader()

    # 连接数据库
    if not db_reader.connect():
        sys.exit(1)

    try:
        # 获取所有表名
        tables = db_reader.get_table_names()
        if not tables:
            print("❌ 未找到任何表")
            return

        print(f"📚 发现 {len(tables)} 个表: {', '.join(tables)}")

        # 显示每个表的信息
        for table in tables:
            db_reader.display_table(table, limit=5)  # 只显示前5行预览

        # 询问是否导出数据
        export_choice = input("\n❓ 是否要将所有表导出为CSV文件？(y/N): ").strip().lower()
        if export_choice == 'y':
            print("\n📤 开始导出数据...")
            for table in tables:
                db_reader.export_to_csv(table)
            print("✅ 所有表已导出完成！")

    except KeyboardInterrupt:
        print("\n⚠️  用户中断操作")
    except Exception as e:
        print(f"❌ 发生错误: {e}")
    finally:
        # 断开连接
        db_reader.disconnect()


if __name__ == "__main__":
    main()
