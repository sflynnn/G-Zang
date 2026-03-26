#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
G-Zang 数据库读取脚本（简化版）
只使用标准库，不需要额外安装依赖
"""

import mysql.connector
from mysql.connector import Error
import sys


class SimpleGZangDBReader:
    """简化的G-Zang数据库读取器"""

    def __init__(self, host='localhost', user='root', password='123456', database='g_zang'):
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

    def read_table(self, table_name, limit=50):
        """读取指定表的内容"""
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

    def get_table_count(self, table_name):
        """获取表的记录数"""
        try:
            cursor = self.connection.cursor()
            cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
            count = cursor.fetchone()[0]
            cursor.close()
            return count
        except Error as e:
            print(f"❌ 获取表 {table_name} 记录数失败: {e}")
            return 0

    def print_table(self, table_name, limit=10):
        """打印表的内容"""
        print(f"\n{'='*60}")
        print(f"📋 表: {table_name}")
        print(f"{'='*60}")

        # 获取记录数
        count = self.get_table_count(table_name)
        print(f"📊 总记录数: {count}")

        # 读取表数据
        column_names, rows = self.read_table(table_name, limit)
        if column_names and rows:
            print(f"\n📝 数据预览 (前{min(limit, len(rows))}行):")

            # 打印表头
            print(" | ".join(f"{col:<15}" for col in column_names))
            print("-" * (len(column_names) * 18 - 1))

            # 打印数据
            for row in rows:
                formatted_row = []
                for value in row:
                    if value is None:
                        formatted_row.append("NULL".ljust(15))
                    else:
                        str_value = str(value)
                        # 截断过长的字符串
                        if len(str_value) > 12:
                            str_value = str_value[:9] + "..."
                        formatted_row.append(str_value.ljust(15))
                print(" | ".join(formatted_row))

            if count > limit:
                print(f"\n... 还有 {count - limit} 行数据未显示")
        elif column_names and not rows:
            print("该表暂无数据")
        print()


def main():
    """主函数"""
    print("🚀 G-Zang 数据库读取工具（简化版）")
    print("=" * 50)

    # 创建数据库读取器
    db_reader = SimpleGZangDBReader()

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
            db_reader.print_table(table, limit=5)  # 只显示前5行预览

        print("💡 提示: 如果需要导出数据，请使用 read_gzang_db.py 脚本（需要安装pandas和tabulate）")

    except KeyboardInterrupt:
        print("\n⚠️  用户中断操作")
    except Exception as e:
        print(f"❌ 发生错误: {e}")
    finally:
        # 断开连接
        db_reader.disconnect()


if __name__ == "__main__":
    main()
