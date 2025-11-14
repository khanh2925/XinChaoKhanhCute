package dao;

import connectDB.connectDB;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import enums.HinhThucKM;
import enums.DuongDung; // 💡 Import mới
import enums.LoaiSanPham; // 💡 Import mới

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChiTietKhuyenMaiSanPham_DAO {

    public ChiTietKhuyenMaiSanPham_DAO() {}

    /** 🔹 Lấy danh sách chi tiết khuyến mãi theo mã khuyến mãi */
    public List<ChiTietKhuyenMaiSanPham> timKiemChiTietKhuyenMaiSanPhamBangMa(String maKM) {
        List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = "SELECT MaSanPham, SoLuongToiThieu, SoLuongTangThem FROM ChiTietKhuyenMaiSanPham WHERE MaKM = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKM);
            try (ResultSet rs = stmt.executeQuery()) {
                SanPham_DAO sanPhamDAO = new SanPham_DAO();
                KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

                while (rs.next()) {
                    String maSP = rs.getString("MaSanPham");
                    int slToiThieu = rs.getInt("SoLuongToiThieu");
                    int slTangThem = rs.getInt("SoLuongTangThem");

                    SanPham sp = sanPhamDAO.laySanPhamTheoMa(maSP);
                    KhuyenMai km = khuyenMaiDAO.timKhuyenMaiTheoMa(maKM);

                    if (sp == null || km == null) {
                        System.err.println("⚠️ Bỏ qua chi tiết khuyến mãi: không tìm thấy SP hoặc KM (" + maKM + ")");
                        continue;
                    }

                    ChiTietKhuyenMaiSanPham ct = new ChiTietKhuyenMaiSanPham(sp, km, slToiThieu, slTangThem);
                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy chi tiết khuyến mãi theo mã: " + e.getMessage());
        }
        return ds;
    }

    /** 🔹 Thêm chi tiết khuyến mãi mới */
    public boolean themChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "INSERT INTO ChiTietKhuyenMaiSanPham (MaKM, MaSanPham, SoLuongToiThieu, SoLuongTangThem) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ctkm.getKhuyenMai().getMaKM());
            stmt.setString(2, ctkm.getSanPham().getMaSanPham());
            stmt.setInt(3, ctkm.getSoLuongToiThieu());
            stmt.setInt(4, ctkm.getSoLuongTangThem());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thêm chi tiết khuyến mãi SP: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Cập nhật số lượng tối thiểu & số lượng tặng thêm */
    public boolean capNhatChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham ctkm) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "UPDATE ChiTietKhuyenMaiSanPham SET SoLuongToiThieu=?, SoLuongTangThem=? WHERE MaKM=? AND MaSanPham=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ctkm.getSoLuongToiThieu());
            ps.setInt(2, ctkm.getSoLuongTangThem());
            ps.setString(3, ctkm.getKhuyenMai().getMaKM());
            ps.setString(4, ctkm.getSanPham().getMaSanPham());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật chi tiết khuyến mãi SP: " + e.getMessage());
        }
        return false;
    }

    /** 🔹 Xóa chi tiết khuyến mãi sản phẩm */
    public boolean xoaChiTietKhuyenMaiSanPham(String maKM, String maSP) {
        connectDB.getInstance();
        Connection con = connectDB.getConnection();
        String sql = "DELETE FROM ChiTietKhuyenMaiSanPham WHERE MaKM=? AND MaSanPham=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xóa chi tiết khuyến mãi SP: " + e.getMessage());
        }
        return false;
    }

    // =============================== JOIN TRUY VẤN TỐI ƯU ===============================

    /**
     * 🔹 Lấy danh sách chi tiết khuyến mãi (JOIN 3 bảng: ChiTiet + SanPham + KhuyenMai)
     * Dùng cho hiển thị GUI: tránh gọi 2 DAO con nhiều lần.
     */
    public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiTheoMaCoJoin(String maKM) {
        List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();
        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT ctkm.MaKM, ctkm.MaSanPham, ctkm.SoLuongToiThieu, ctkm.SoLuongTangThem,
                   sp.TenSanPham, sp.GiaNhap, sp.KeBanSanPham, sp.HoatDong,
                   km.TenKM, km.HinhThuc, km.GiaTri, km.TrangThai
            FROM ChiTietKhuyenMaiSanPham ctkm
            JOIN SanPham sp ON ctkm.MaSanPham = sp.MaSanPham
            JOIN KhuyenMai km ON ctkm.MaKM = km.MaKM
            WHERE ctkm.MaKM = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Tạo SanPham tạm (đủ dùng cho GUI)
                    SanPham sp = new SanPham(
                        rs.getString("MaSanPham"),
                        rs.getString("TenSanPham"),
                        null, // LoaiSanPham không cần ở GUI này
                        null,
                        null,
                        rs.getDouble("GiaNhap"),
                        null,
                        rs.getString("KeBanSanPham"),
                        rs.getBoolean("HoatDong")
                    );

                    // Tạo KhuyenMai tạm
                    HinhThucKM hinhThuc = null;
                    String hinhThucStr = rs.getString("HinhThuc");
                    if (hinhThucStr != null) {
                        try { hinhThuc = HinhThucKM.valueOf(hinhThucStr.trim().toUpperCase()); } catch (Exception ignored) {}
                    }

                    KhuyenMai km = new KhuyenMai(
                        rs.getString("MaKM"),
                        rs.getString("TenKM"),
                        null, null,
                        rs.getBoolean("TrangThai"),
                        false,
                        hinhThuc,
                        rs.getDouble("GiaTri"),
                        0,
                        0
                    );

                    int slToiThieu = rs.getInt("SoLuongToiThieu");
                    int slTangThem = rs.getInt("SoLuongTangThem");

                    ChiTietKhuyenMaiSanPham ct = new ChiTietKhuyenMaiSanPham(sp, km, slToiThieu, slTangThem);
                    ds.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi JOIN chi tiết khuyến mãi SP: " + e.getMessage());
        }
        return ds;
    }
    
    /**
     * 🔹 Lấy danh sách chi tiết khuyến mãi đang hoạt động theo MÃ SẢN PHẨM (JOIN 3 bảng)
     * Dùng để tìm khuyến mãi đang áp dụng cho một sản phẩm.
     */
    public List<ChiTietKhuyenMaiSanPham> layChiTietKhuyenMaiDangHoatDongTheoMaSP(String maSP) {
        List<ChiTietKhuyenMaiSanPham> ds = new ArrayList<>();

        connectDB.getInstance();
        Connection con = connectDB.getConnection();

        String sql = """
            SELECT ctkm.MaKM, ctkm.MaSanPham, ctkm.SoLuongToiThieu, ctkm.SoLuongTangThem,
                   sp.TenSanPham, sp.GiaNhap, sp.KeBanSanPham, sp.HoatDong, sp.LoaiSanPham, sp.DuongDung, 
                   km.TenKM, km.HinhThuc, km.GiaTri, km.TrangThai, km.NgayBatDau, km.NgayKetThuc, 
                   km.SoLuongKhuyenMai, km.KhuyenMaiHoaDon
            FROM ChiTietKhuyenMaiSanPham ctkm
            JOIN SanPham sp ON ctkm.MaSanPham = sp.MaSanPham
            JOIN KhuyenMai km ON ctkm.MaKM = km.MaKM
            WHERE ctkm.MaSanPham = ?
              AND km.TrangThai = 1
              AND GETDATE() BETWEEN km.NgayBatDau AND km.NgayKetThuc
              AND km.SoLuongKhuyenMai > 0
              AND km.KhuyenMaiHoaDon = 0
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSP);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // 1. SanPham
                    SanPham sp = new SanPham();
                    sp.setMaSanPham(rs.getString("MaSanPham"));
                    sp.setTenSanPham(rs.getString("TenSanPham"));
                    // nếu cần thêm thuộc tính khác thì set tiếp

                    // 2. KhuyenMai
                    KhuyenMai km = new KhuyenMai();
                    km.setMaKM(rs.getString("MaKM"));
                    km.setTenKM(rs.getString("TenKM"));
                    km.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
                    km.setNgayKetThuc(rs.getDate("NgayKetThuc").toLocalDate());
                    km.setTrangThai(rs.getBoolean("TrangThai"));
                    km.setKhuyenMaiHoaDon(rs.getBoolean("KhuyenMaiHoaDon"));

                    String hinhThucStr = rs.getString("HinhThuc");
                    if (hinhThucStr != null) {
                        km.setHinhThuc(HinhThucKM.valueOf(hinhThucStr.trim().toUpperCase()));
                    }

                    km.setGiaTri(rs.getDouble("GiaTri"));
                    // Nếu trong DB có cột DieuKienApDungHoaDon thì SELECT thêm rồi mở dòng dưới:
                    // km.setDieuKienApDungHoaDon(rs.getDouble("DieuKienApDungHoaDon"));
                    km.setSoLuongKhuyenMai(rs.getInt("SoLuongKhuyenMai"));

                    // 3. ChiTietKhuyenMaiSanPham
                    ChiTietKhuyenMaiSanPham ct = new ChiTietKhuyenMaiSanPham();
                    ct.setSanPham(sp);
                    ct.setKhuyenMai(km);
                    ct.setSoLuongToiThieu(rs.getInt("SoLuongToiThieu"));
                    ct.setSoLuongTangThem(rs.getInt("SoLuongTangThem"));

                    ds.add(ct);
                }
            }

            System.out.println("DAO - MaSP=" + maSP + ", so CTKM lay duoc=" + ds.size());

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi JOIN chi tiết khuyến mãi SP: " + e.getMessage());
        }

        return ds;
    }


}