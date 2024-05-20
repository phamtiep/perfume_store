import React, { FC, ReactElement } from "react";
import { FacebookOutlined, LinkedinOutlined, TwitterOutlined } from "@ant-design/icons";
import { Col, Row, Typography } from "antd";

import "./Footer.scss";

const Footer: FC = (): ReactElement => {
    return (
        <div className={"footer-wrapper"}>
            <Row >
                <Col span={12}>
                    <Typography.Title level={3}>Frieren Perfume Store</Typography.Title>
                    <Typography.Text>Phát triển bởi : Pham Tiep - </Typography.Text>
                    <Typography.Text>Viet Anh - </Typography.Text>
                    <Typography.Text>Long Vinh</Typography.Text>
                
                </Col>
            </Row>
        </div>
    );
};

export default Footer;
