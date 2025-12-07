package com.example.minecraft.dao;

import com.example.minecraft.dto.ServerImageDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerImageDAO {

    final String SQL_IMAGE_CREATE = "INSERT INTO server_image (server_id, file_name, original_name, file_path, file_size) VALUES (?, ?, ?, ?, ?);";
    final String SQL_IMAGE_SELECT_BY_SERVER_ID = "SELECT * FROM server_image WHERE server_id = ?;";

    public int createServerImage(Connection con, ServerImageDTO imageDTO) throws SQLException {

        int result = 0;

        try (PreparedStatement pstmt = con.prepareStatement(SQL_IMAGE_CREATE)) {
            pstmt.setLong(1, imageDTO.getServerId());
            pstmt.setString(2, imageDTO.getFileName());
            pstmt.setString(3, imageDTO.getOriginalName());
            pstmt.setString(4, imageDTO.getFilePath());
            pstmt.setLong(5, imageDTO.getFileSize());

            result = pstmt.executeUpdate();

        }

        return result;
    }

    /**
     * 서버 ID를 기준으로 이미지 정보를 조회합니다.
     * @param con Connection 객체 (Service에서 전달받음)
     * @param serverId 조회할 서버의 ID
     * @return ServerImageDTO (이미지가 없으면 null)
     */
    public ServerImageDTO getServerImageByServerId(Connection con, Long serverId) {
        ServerImageDTO imageDTO = null;

        // try-with-resources를 사용하여 PreparedStatement와 ResultSet 자동 해제
        try (PreparedStatement pstmt = con.prepareStatement(SQL_IMAGE_SELECT_BY_SERVER_ID)) {
            pstmt.setLong(1, serverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    imageDTO = new ServerImageDTO();
                    imageDTO.setImageId(rs.getLong("image_id"));
                    imageDTO.setServerId(rs.getLong("server_id"));
                    imageDTO.setFileName(rs.getString("file_name"));
                    imageDTO.setOriginalName(rs.getString("original_name"));
                    imageDTO.setFilePath(rs.getString("file_path"));
                    imageDTO.setFileSize(rs.getLong("file_size"));
                    imageDTO.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageDTO;
    }
}