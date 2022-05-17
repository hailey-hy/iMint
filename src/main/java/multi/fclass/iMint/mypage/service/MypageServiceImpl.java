package multi.fclass.iMint.mypage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import multi.fclass.iMint.mypage.dao.IMypageDAO;
import multi.fclass.iMint.mypage.dto.MypageBlockDTO;
import multi.fclass.iMint.mypage.dto.MypageChatroomDTO;
import multi.fclass.iMint.mypage.dto.MypageConnectionDTO;
import multi.fclass.iMint.mypage.dto.MypageDTO;

/**
 * @author GhostFairy
 *
 */
@Service
public class MypageServiceImpl implements IMypageService {

	@Autowired
	IMypageDAO mypageDAO;

	// 내 보호자 조회 서비스
	@Override
	public MypageConnectionDTO getMyGuard(String myId) {
		return mypageDAO.getMyGuard(myId);
	}

	// 내 아이 목록 조회 서비스
	@Override
	public List<MypageConnectionDTO> getMyChildrenList(String myId) {
		return mypageDAO.getMyChildrenList(myId);
	}

	@Override
	public boolean isMyChild(String myId, String childId) {
		boolean flag = false;
		List<MypageConnectionDTO> childrenList = getMyChildrenList(myId);

		for (MypageConnectionDTO child : childrenList) {
			if (child.getMbId().equals(childId)) {
				return true;
			}
		}

		return flag;
	}

	// 관심 목록 조회 서비스
	@Override
	public List<MypageDTO> getWishList(String myId) {
		return mypageDAO.getWishList(myId);
	}

	// 구매예약/판매중 목록 조회 서비스
	@Override
	public List<MypageDTO> getTradeList(String myId) {
		return mypageDAO.getTradeList(myId);
	}

	// 거래완료 목록 조회 서비스
	@Override
	public List<MypageDTO> getCompleteList(String myId) {
		return mypageDAO.getCompleteList(myId);
	}

	// 채팅방 목록 조회 서비스
	@Override
	public List<MypageChatroomDTO> getChatroomList(String myId) {
		return mypageDAO.getChatroomList(myId);
	}

	// 차단 목록 조회 서비스
	@Override
	public List<MypageBlockDTO> getBlockList(String myId) {
		return mypageDAO.getBlockList(myId);
	}

}
