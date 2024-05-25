import React, { FC, ReactElement, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Carousel, Typography } from "antd";

import { selectPerfumes } from "../../../redux-toolkit/perfumes/perfumes-selector";
import { fetchPerfumesByIds } from "../../../redux-toolkit/perfumes/perfumes-thunks";
import { resetPerfumesState } from "../../../redux-toolkit/perfumes/perfumes-slice";
import PerfumeCardsSliderItem from "./PerfumeCardsSliderItem/PerfumeCardsSliderItem";
import "./PerfumeCardsSlider.css";
import { selectUserFromUserState } from "../../../redux-toolkit/user/user-selector";
import RequestService from "../../../utils/request-service";
import axios from "axios";

export let perfumesIds = [26, 43, 46, 106, 34, 76, 82, 85, 27, 39, 79, 86];

const PerfumeCardsSlider: FC = (): ReactElement => {
    const dispatch = useDispatch();
    const perfumes = useSelector(selectPerfumes);
    const usersData = useSelector(selectUserFromUserState);
    const nameUser = usersData?.firstName;

    useEffect(() => {
        const fetchRecommendedPerfumes = async () => {
            try {

                const response = await RequestService.get1("http://192.168.1.58:8000/recommend/" + nameUser, true)
                perfumesIds = response.data;
                console.log(perfumesIds)
                dispatch(fetchPerfumesByIds(perfumesIds));
            } catch (error) {
                console.error("Failed to fetch recommended perfumes", error);
            }
        };

        fetchRecommendedPerfumes();

        return () => {
            dispatch(resetPerfumesState());
        };
    }, [dispatch]);

    return (
        <div className={"perfume-cards-slider"}>
            <Typography.Title level={3} className={"perfume-cards-slider-title"}>
                CÁC SẢN PHẨM PHÙ HỢP VỚI BẠN
            </Typography.Title>
            <Carousel>
                <PerfumeCardsSliderItem perfumes={perfumes.slice(0, 4)} />
                <PerfumeCardsSliderItem perfumes={perfumes.slice(4, 8)} />
                <PerfumeCardsSliderItem perfumes={perfumes.slice(8, 12)} />
            </Carousel>
        </div>
    );
};

export default PerfumeCardsSlider;
