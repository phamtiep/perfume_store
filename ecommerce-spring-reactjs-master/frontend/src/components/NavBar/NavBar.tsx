import React, { FC, ReactElement } from "react";
import { useDispatch, useSelector } from "react-redux";
import { LoginOutlined, LogoutOutlined, ShoppingCartOutlined, UserAddOutlined, UserOutlined } from "@ant-design/icons";
import { Link } from "react-router-dom";
import { Affix, Badge, Col, Row, Space } from "antd";

import { selectUserFromUserState } from "../../redux-toolkit/user/user-selector";
import { selectCartItemsCount } from "../../redux-toolkit/cart/cart-selector";
import { logoutSuccess } from "../../redux-toolkit/user/user-slice";
import { ACCOUNT, BASE, CONTACTS, LOGIN, MENU, REGISTRATION } from "../../constants/routeConstants";
import { CART } from "../../constants/urlConstants";
import "./NavBar.scss";
import logo2 from "../../img/perfumelogo.png"
import logo from "../../img/logo2.png"
const NavBar: FC = (): ReactElement => {
    const dispatch = useDispatch();
    const usersData = useSelector(selectUserFromUserState);
    const cartItemsCount = useSelector(selectCartItemsCount);

    const handleLogout = (): void => {
        localStorage.removeItem("token");
        dispatch(logoutSuccess());
    };

    return (
        <>
            <div className={"navbar-logo-wrapper"}>
                <img alt={"navbar-logo"} src={logo} />
            </div>
            <Affix>
                <div className={"navbar-wrapper"}>
                    <Row style={{ padding: "0px 400px" }}>
                        <Row >
                            <ul>
                                <Link to={BASE}>
                                    <li>TRANG CHỦ </li>
                                </Link>
                                <li>
                                    <Link to={{ pathname: MENU, state: { id: "all" } }}> BỘ SƯU TẬP </Link>
                                </li>
                                <li className={"navbar-cart"}>
                                    <Badge count={cartItemsCount} size="small" color={"green"}>
                                        <Link to={CART}>
                                            <ShoppingCartOutlined />
                                        </Link>
                                    </Badge>
                                </li>
                                {usersData ? (
                                    <>
                                        <Link to={ACCOUNT}>
                                            <li>
                                                <UserOutlined />
                                                TÀI KHOẢN CỦA TÔI
                                            </li>
                                        </Link>
                                        <Link id={"handleLogout"} to={BASE} onClick={handleLogout}>
                                            <li>
                                                <LogoutOutlined />
                                                THOÁT 
                                            </li>
                                        </Link>
                                    </>
                                ) : (
                                    <>
                                        <Link to={LOGIN}>
                                            <li>
                                                <Space align={"baseline"}>
                                                    <LoginOutlined />
                                                    ĐĂNG NHẬP 
                                                </Space>
                                            </li>
                                        </Link>
                                        <Link to={REGISTRATION}>
                                            <li>
                                                <UserAddOutlined />
                                                ĐĂNG KÍ 
                                            </li>
                                        </Link>
                                    </>
                                )}

                            </ul>
                        </Row>
                
                    </Row>
                </div>
            </Affix>
        </>
    );
};

export default NavBar;
