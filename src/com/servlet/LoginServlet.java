package com.servlet;

import com.dao.AdminDao;
import com.pojo.Admin;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");

        // 获取请求参数
        Map<String, String[]> map = req.getParameterMap();

        // 创建 admin 对象
        Admin admin = new Admin();

        // 将 map 中的请求参数封装到 admin 对象中
        try {
            BeanUtils.populate(admin, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // 获取流对象，读取核心配置文件
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        // 创建 sqlSessionFactoryBuilder 工厂构建者对象
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        // 获取 sqlSessionFactory 工厂对象
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
        // 获取 SqlSession 对象
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 获取 AdminDao 代理类对象
        AdminDao adminDao = sqlSession.getMapper(AdminDao.class);
        // 调用 adminDao
        Admin result = adminDao.findByNameAndPassword(admin);
        // sqlSession 提交
        sqlSession.commit();
        // sqlSession 关闭
        sqlSession.close();

        // 判断 result 是否为 null
        if (result == null) {
            // 没有该用户，跳转到 login.html
            req.getRequestDispatcher("admin.jsp").forward(req, resp);
        } else {
            // 有该用户，跳转到 welcome.html
            req.getSession().setAttribute("admin",admin);
            req.getRequestDispatcher("admin.jsp").forward(req, resp);
        }
    }
}